package org.yah.meccanobuilder.model.importer.pdf;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

import java.awt.geom.Point2D;
import java.io.IOException;

public abstract class PDFGraphicsStreamEngineAdapter extends PDFGraphicsStreamEngine {

    protected final Point2D currentPoint = new Point2D.Float();

    protected PDFGraphicsStreamEngineAdapter(PDPage page) {
        super(page);
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
        //no op
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException {
        //no op
    }

    @Override
    public void clip(int windingRule) throws IOException {
        //no op
    }

    @Override
    public void moveTo(float x, float y) throws IOException {
        //no op
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
        //no op
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        //no op
    }

    @Override
    public Point2D getCurrentPoint() throws IOException {
        return currentPoint;
    }

    @Override
    public void closePath() throws IOException {
        //no op
    }

    @Override
    public void endPath() throws IOException {
        //no op
    }

    @Override
    public void strokePath() throws IOException {
        //no op
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
        //no op
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
        //no op
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
        //no op
    }
}
