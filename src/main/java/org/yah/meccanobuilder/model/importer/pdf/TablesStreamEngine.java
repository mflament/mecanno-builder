package org.yah.meccanobuilder.model.importer.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.yah.meccanobuilder.model.importer.pdf.PDFTables.DimensionSeparators;
import org.yah.meccanobuilder.model.importer.pdf.PDFTables.PositionSeparators;
import org.yah.meccanobuilder.model.importer.pdf.PDFTables.Separator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TablesStreamEngine extends PDFGraphicsStreamEngineAdapter {

    private static final Stroke STROKE = new BasicStroke(1);

    private final LinkedList<Rectangle2D> pendingRectangles = new LinkedList<>();
    private final PDFTables tables;
    private final double pageWidth;
    private final double pageHeight;
    private DebugPanel debugPanel;

    public TablesStreamEngine(PDFTables tables, boolean debug) {
        super(tables.getPage());
        this.tables = tables;
        pageWidth = tables.getPageWidth();
        pageHeight = tables.getPageHeight();
        if (debug) {
            SwingUtilities.invokeLater(() -> debugPanel = createDebugPanel());
        }
    }

    /**
     * p0 ---------- p1
     * |             |
     * |             |
     * p3----------- P2
     */
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        double w = p1.getX() - p0.getX();
        double h = p0.getY() - p3.getY();
        pendingRectangles.add(new Rectangle2D.Double(p3.getX(), p3.getY(), w, h));
        refreshPanel();
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        super.processPage(page);
        tables.filterMainTable();
        refreshPanel();
    }

    @Override
    public void fillPath(int windingRule) {
        if (pendingRectangles.isEmpty())
            return;

        while (!pendingRectangles.isEmpty()) {
            final Rectangle2D rectangle = pendingRectangles.getFirst();
            tables.insert(rectangle);
            refreshPanel();
            pendingRectangles.removeFirst();
        }
        refreshPanel();
    }

    @Override
    public void endPath() {
        pendingRectangles.clear();
        refreshPanel();
    }

    private void refreshPanel() {
        if (debugPanel != null) {
            debugPanel.refresh();
        }
    }

    private DebugPanel createDebugPanel() {
        final JFrame frame = new JFrame("PDF rectangles");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DebugPanel panel = new DebugPanel();
        panel.setPreferredSize(new Dimension((int) pageWidth, (int) pageHeight));
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        return panel;
    }

    private final class DebugPanel extends JPanel {
        List<Rectangle2D> rectangles = List.copyOf(pendingRectangles);
        DimensionSeparators columnSeparators = new DimensionSeparators(tables.getColumnSeparators());
        DimensionSeparators rowSeparators = new DimensionSeparators(tables.getRowSeparators());

        @Override
        public void paint(Graphics g) {
            drawSeparators((Graphics2D) g);
        }

        public void refresh() {
            if (SwingUtilities.isEventDispatchThread()) {
                revalidate();
                repaint();
            } else {
                rectangles = List.copyOf(pendingRectangles);
                columnSeparators = new DimensionSeparators(tables.getColumnSeparators());
                rowSeparators = new DimensionSeparators(tables.getRowSeparators());
                SwingUtilities.invokeLater(this::refresh);
            }
        }

        private void drawSeparators(Graphics2D g) {
            final Rectangle clipBounds = g.getClipBounds();
            int w = (int) clipBounds.getWidth();
            int h = (int) clipBounds.getHeight();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);

            g.setStroke(STROKE);
            g.setColor(Color.GREEN);
            for (Rectangle2D rectangle : rectangles) {
                g.drawRect(round(rectangle.getX()), round(pageHeight - rectangle.getMaxY()),
                        round(rectangle.getWidth()), round(rectangle.getHeight()));
            }

            g.setColor(Color.BLACK);
            columnSeparators.forEach(cs -> drawSeparators(g, cs, false));
            rowSeparators.forEach(cs -> drawSeparators(g, cs, true));
        }

        private void drawSeparators(Graphics2D g, PositionSeparators separators, boolean row) {
            for (int i = 0; i < separators.size(); i++) {
                Separator separator = separators.get(i);
                final int x1, y1, x2, y2;
                //flip y coordinate to flip image (PDF 'y' is bottom -> top, Graphics2D is top -> bottom)
                if (row) {
                    y1 = y2 = round(pageHeight - separators.pos());
                    x1 = round(separator.start());
                    x2 = round(separator.end());
                } else {
                    x1 = x2 = round(separators.pos());
                    y1 = round(pageHeight - separator.end());
                    y2 = round(pageHeight - separator.start());
                }
                g.drawLine(x1, y1, x2, y2);
            }
        }

    }

    private static int round(double d) {
        return (int) (d + 0.5);
    }
}
