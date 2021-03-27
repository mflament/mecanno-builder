package org.yah.meccanobuilder.model.repository;

import org.yah.meccanobuilder.model.entitites.*;
import org.yah.meccanobuilder.model.entitites.SetPart.SetPartId;

import java.util.*;
import java.util.stream.Collectors;

public class PartsRepository {

    private final Map<String, MeccanoSet> sets = new LinkedHashMap<>();
    private final Map<String, Part> parts = new LinkedHashMap<>();
    private final Map<SetPartId, List<SetPart>> setParts = new LinkedHashMap<>();
    private final Map<String, PartColor> colors = new LinkedHashMap<>();
    private final Map<String, PartMaterial> materials = new LinkedHashMap<>();

    public void saveSet(MeccanoSet set) {
        sets.put(set.getId(), set);
    }

    public void savePart(Part part) {
        parts.put(part.getId(), part);
    }

    public void saveColor(PartColor color) {
        colors.put(color.getId(), color);
    }

    public void saveMaterial(PartMaterial material) {
        materials.put(material.getId(), material);
    }

    public void saveSetPart(SetPart setPart) {
        getSet(setPart.getSetId());
        getPart(setPart.getPartId());
        setPart.getColorIds().forEach(this::getColor);
        if (setPart.getMaterialId() != null)
            getMaterial(setPart.getMaterialId());
        final var sps = setParts.computeIfAbsent(setPart.getId(), id -> new ArrayList<>());
        sps.add(setPart);
    }

    public MeccanoSet getSet(String id) {
        return get("Set", id, sets);
    }

    public Part getPart(String id) {
        return get("Part", id, parts);
    }

    public PartColor getColor(String id) {
        return get("Color", id, colors);
    }

    public PartMaterial getMaterial(String id) {
        return get("Material", id, materials);
    }

    public List<SetPart> getSetPart(SetPartId id) {
        return get("SetPart", id, setParts);
    }

    public Optional<MeccanoSet> findSet(String number) {
        return Optional.ofNullable(sets.get(number));
    }

    public Optional<Part> findPart(String id) {
        return Optional.ofNullable(parts.get(id));
    }

    public Optional<PartColor> findColor(String id) {
        return Optional.ofNullable(colors.get(id));
    }

    public Optional<PartMaterial> findMaterial(String id) {
        return Optional.ofNullable(materials.get(id));
    }

    public List<SetPart> getSetParts(String setId, String partId) {
        return getSetPart(new SetPartId(setId, partId));
    }

    public List<MeccanoSet> getSets() {
        return List.copyOf(sets.values());
    }

    public List<Part> getParts() {
        return List.copyOf(parts.values());
    }

    public List<PartColor> getColors() {
        return List.copyOf(colors.values());
    }

    public List<PartMaterial> getMaterials() {
        return List.copyOf(materials.values());
    }

    public List<SetPart> getSetParts() {
        return setParts.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public Optional<Part> findPartsByName(String partName) {
        return parts.values().stream().filter(p -> p.getName().equals(partName)).findFirst();
    }

    private static <K, T> T get(String name, K key, Map<K, T> values) {
        final T t = values.get(key);
        if (t == null)
            throw new NoSuchElementException(name + " '" + key + "' not found");
        return t;
    }
}
