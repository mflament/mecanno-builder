package org.yah.meccanobuilder.model.importer.pdf;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.yah.meccanobuilder.model.importer.pdf.TableCoordinatesResolver.TableCoordinates;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PDFImporter {

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
//        final File file = PDF_DIRECTORY.resolve("ConvTabNew-OldRev24p.pdf").toFile();
        final File file = PDF_DIRECTORY.resolve("NewParts70-81UK_01.pdf").toFile();
        final PDDocument document = parsePDF(file);

        final PDPage page = document.getPage(0);
        final PDFTables tables = new PDFTables(page);
        PDFStreamEngine engine = new TablesStreamEngine(tables, false);
        engine.processPage(page);

        final Map<TableCoordinates, StringBuilder> builders = new HashMap<>();
        engine = new ContentStreamEngine(page, tables, (coordinates, text) -> {
            final StringBuilder sb = builders.computeIfAbsent(coordinates, c -> new StringBuilder());
            sb.append(text);
        });
        engine.processPage(page);

        builders.forEach((c,t) -> System.out.println(c + " : " + t));

        document.close();

//        PDFRenderer renderer = new PDFRenderer(document);
//        final BufferedImage bufferedImage = renderer.renderImage(0);
//        display(bufferedImage);

//        for (PDPage page : document.getPages()) {
//            final TableStreamEngine engine = new TableStreamEngine(page);
//            engine.processPage(page);
//            break;
//        }
    }

    private static void display(BufferedImage bufferedImage) throws InvocationTargetException, InterruptedException {
        final JFrame frame = new JFrame("image");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(bufferedImage,0,0, null);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
            }
        });
        SwingUtilities.invokeAndWait(() -> frame.setVisible(true));
    }

    private static PDDocument parsePDF(File file) throws IOException {
        final PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
        parser.parse();
        final PDDocument document = parser.getPDDocument();
        return document;
    }

    private static final Path PDF_DIRECTORY = Path.of("docs/meccano.planetaclix.pt");

}
