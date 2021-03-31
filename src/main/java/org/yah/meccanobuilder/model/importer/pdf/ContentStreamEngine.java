package org.yah.meccanobuilder.model.importer.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.util.Matrix;
import org.yah.meccanobuilder.model.importer.pdf.TableCoordinatesResolver.TableCoordinates;

import java.io.IOException;
import java.util.Objects;

public class ContentStreamEngine extends PDFGraphicsStreamEngineAdapter {

    private final TableCoordinatesResolver coordinatesResolver;
    private final TableDataCollector dataCollector;

    protected ContentStreamEngine(PDPage page, TableCoordinatesResolver coordinatesResolver, TableDataCollector dataCollector) {
        super(page);
        this.coordinatesResolver = Objects.requireNonNull(coordinatesResolver, "coordinatesResolver is null");
        this.dataCollector = Objects.requireNonNull(dataCollector, "dataCollector is null");
    }

    @Override
    protected void showText(byte[] bytes) throws IOException {
        super.showText(bytes);
        final String text = new String(bytes);
        final Matrix textMatrix = getTextMatrix();
        double x = textMatrix.getTranslateY();
        double y = textMatrix.getTranslateY();
        final TableCoordinates coordinates = coordinatesResolver.resovle(x, y);
        dataCollector.withText(coordinates, text);
    }

    @Override
    public void drawImage(PDImage pdImage) {
        // TODO handle image, add method to dataCollector
    }
}
