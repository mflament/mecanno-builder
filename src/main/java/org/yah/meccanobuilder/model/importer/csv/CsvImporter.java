package org.yah.meccanobuilder.model.importer.csv;

import com.Ostermiller.util.ExcelCSVParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yah.meccanobuilder.model.entitites.*;
import org.yah.meccanobuilder.model.repository.JsonParts;
import org.yah.meccanobuilder.model.repository.PartsRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public class CsvImporter {

    public static void main(String[] args) throws IOException {
        final CsvImporter importer = new CsvImporter();
        final PartsRepository repository = importer.importRepository();
        final JsonParts jsonParts = JsonParts.builder()
                .withColors(repository.getColors())
                .withMaterials(repository.getMaterials())
                .withSets(repository.getSets())
                .withParts(repository.getParts())
                .withSetPart(repository.getSetParts())
                .build();

        final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule());

        objectMapper.writeValue(new File("meccano-parts.json"), jsonParts);

        LOGGER.info("sets: {}", repository.getSets().size());
        LOGGER.info("parts: {}", repository.getParts().size());
        LOGGER.info("colors: {}", repository.getColors().size());
        LOGGER.info("materials: {}", repository.getMaterials().size());
        LOGGER.info("set parts: {}", repository.getSetParts().size());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvImporter.class);

    private static final Path DATA = Path.of("data/meccanoindex");

    private static final List<Path> DIRECTORIES = List.of(
            DATA.resolve("1970-1981"),
            DATA.resolve("1989-2000"),
            DATA.resolve("1998-2014"),
            DATA.resolve("2014-2016"));

    private static final Pattern ADDITION_PATTERN = Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)");

    private final PartsRepository repository;
    private int nextUnknownSetId = 1;
    private int nextUnknownPartId = 1;

    public CsvImporter() {
        repository = new PartsRepository();
    }

    public PartsRepository importRepository() throws IOException {
        importColors();
        importMaterials();
        for (Path directory : DIRECTORIES) {
            importParts(directory);
        }
        return repository;
    }

    private void importColors() throws IOException {
        final Path file = DATA.resolve("colors.csv");
        final String[][] values = loadCsv(file);
        for (String[] columns : values) {
            final PartColor color = new PartColor(columns[0].trim(), columns[1].trim());
            repository.findColor(color.getId()).ifPresent(actual -> {
                throw new IllegalStateException("Duplicate color " + color.getId());
            });
            repository.saveColor(color);
        }
    }

    private void importMaterials() throws IOException {
        final Path file = DATA.resolve("materials.csv");
        final String[][] values = loadCsv(file);
        for (String[] columns : values) {
            final PartMaterial material = new PartMaterial(columns[0].trim(), columns[1].trim());
            repository.findMaterial(material.getId()).ifPresent(actual -> {
                throw new IllegalStateException("Duplicate material " + material.getId());
            });

            repository.saveMaterial(material);
        }
    }

    private void importParts(Path directory) throws IOException {
        final String[][] values = loadCsv(directory.resolve("parts.csv"));
        try {
            final MeccanoSet[] sets = parseSets(values);
            final Part[] parts = parseParts(values);
            parseSetParts(sets, parts, values);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error parsing parts " + directory.getFileName(), e);
        }
    }

    private MeccanoSet[] parseSets(String[][] values) {
        String serie = null;
        final List<MeccanoSet> sets = new ArrayList<>(values[0].length - 7);
        for (int col = 7; col < values[0].length; col++) {
            String s = trimToNull(values[0][col]);
            if (s != null)
                serie = s;
            if (serie == null)
                throw new IllegalStateException("No serie");

            String setNumber = trimToNull(values[1][col]);
            if (setNumber == null)
                setNumber = "nonbr:" + (nextUnknownSetId++);
            String name = values[2][col].trim();
            s = trimToNull(values[3][col]);
            if (s != null)
                name += " " + s;
            final int released = parseInt(values[4][col]);
            final String year = trimToNull(values[5][col]);
            final Integer phasedout = year == null ? null : parseInt(year);
            MeccanoSet set = repository.findSet(setNumber).orElse(null);
            if (set == null) {
                set = MeccanoSet.builder()
                        .withNumber(setNumber)
                        .withName(name)
                        .withSerie(serie)
                        .withYearReleased(released)
                        .withYearPhasedOut(phasedout)
                        .build();
                repository.saveSet(set);
            }
            sets.add(set);
        }
        return sets.toArray(MeccanoSet[]::new);
    }

    private Part[] parseParts(String[][] values) {
        final List<Part> parts = new ArrayList<>(values.length - 6);
        for (int row = 6; row < values.length; row++) {
            final String[] columns = values[row];
            final String partName = columns[5].trim();
            final String partNumber = parsePartNumber(columns);
            Part part = repository.findPart(partNumber).orElse(null);
            if (part == null) {
                part = Part.builder()
                        .withNumber(partNumber)
                        .withName(partName)
                        .build();
                repository.savePart(part);
            }
            parts.add(part);
        }
        return parts.toArray(Part[]::new);
    }

    private String parsePartNumber(String[] columns) {
        String number = trimToNull(columns[0] + columns[1]);
        if (number == null)
            number = trimToNull(columns[2]); // new number
        if (number == null)
            number = "unknown:" + (nextUnknownPartId++);
        return number;
    }

    private void parseSetParts(MeccanoSet[] sets, Part[] parts, String[][] values) {
        for (int row = 6; row < values.length; row++) {
            String[] columns = values[row];
            final String partId = parts[row - 6].getId();
            final String[] colorIds = parseColors(columns[3]);
            final String materialId = trimToNull(columns[4]);
            for (int col = 7; col < columns.length; col++) {
                final int count = parseCount(columns[col]);
                if (count > 0) {
                    final String setId = sets[col - 7].getId();
                    final SetPart setPart = SetPart.builder()
                            .withSetId(setId)
                            .withPartId(partId)
                            .withColorIds(colorIds)
                            .withMaterialId(materialId)
                            .withCount(count)
                            .build();
                    repository.saveSetPart(setPart);
                }
            }
        }
    }

    private String[] parseColors(String column) {
        return Arrays.stream(column.split("/"))
                .map(StringUtils::trimToNull)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

    private int parseCount(String column) {
        column = column.trim();
        if (column.length() == 0)
            return 0;
        if (column.equals("*"))
            return 1;
        final Matcher matcher = ADDITION_PATTERN.matcher(column);
        if (matcher.matches()) {
            return parseInt(matcher.group(1)) + parseInt(matcher.group(2));
        }
        return parseInt(column);
    }

    private String[][] loadCsv(Path path) throws IOException {
        try (InputStream is = new FileInputStream(path.toFile())) {
            final ExcelCSVParser csvParser = new ExcelCSVParser(new InputStreamReader(is, StandardCharsets.UTF_8));
            csvParser.changeDelimiter(';');
            return csvParser.getAllValues();
        }
    }
}
