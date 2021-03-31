package org.yah.meccanobuilder.model.importer.pdf;

import org.yah.meccanobuilder.model.importer.pdf.TableCoordinatesResolver.TableCoordinates;

public interface TableDataCollector {
    void withText(TableCoordinates coordinates, String text);
}
