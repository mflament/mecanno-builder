package org.yah.meccanobuilder.model.importer.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class PDFTables implements TableCoordinatesResolver {

    private static final Comparator<Double> COLUMN_COMPARATOR = PDFTables::compare;
    private static final Comparator<Double> ROW_COMPARATOR = COLUMN_COMPARATOR.reversed();

    private static final double MIN_ROW_HEIGHT = 10;
    private static final double MIN_COL_WIDTH = 10;
    private final DimensionSeparators columnSeparators = new DimensionSeparators(COLUMN_COMPARATOR);
    private final DimensionSeparators rowSeparators = new DimensionSeparators(ROW_COMPARATOR);
    private final double pageWidth;
    private final double pageHeight;
    private final PDPage page;

    public PDFTables(PDPage page) {
        this.page = page;
        final PDRectangle cropBox = page.getCropBox();
        this.pageWidth = cropBox.getWidth();
        this.pageHeight = cropBox.getHeight();
    }

    public double getPageWidth() {
        return pageWidth;
    }

    public double getPageHeight() {
        return pageHeight;
    }

    public void insert(Rectangle2D rectangle) {
        if (rectangle.getWidth() > MIN_COL_WIDTH && rectangle.getHeight() > MIN_ROW_HEIGHT) {
            insertColumn(rectangle.getX(), rectangle.getY(), rectangle.getHeight());
            insertColumn(rectangle.getX() + rectangle.getWidth(), rectangle.getY(), rectangle.getHeight());
            insertRow(rectangle.getY(), rectangle.getX(), rectangle.getWidth());
            insertRow(rectangle.getY() + rectangle.getHeight(), rectangle.getX(), rectangle.getWidth());
        } else if (rectangle.getWidth() > MIN_COL_WIDTH) {
            insertRow(rectangle.getY() + rectangle.getHeight() / 2, rectangle.getX(), rectangle.getWidth());
        } else if (rectangle.getHeight() > MIN_ROW_HEIGHT) {
            insertColumn(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY(), rectangle.getHeight());
        }
    }

    public DimensionSeparators getColumnSeparators() {
        return columnSeparators;
    }

    public DimensionSeparators getRowSeparators() {
        return rowSeparators;
    }

    public void filterMainTable() {
        if (columnSeparators.isEmpty()) return;
        final PositionSeparators tablesStart = columnSeparators.get(0);
        final Separator separator = tablesStart.stream().max(Comparator.comparing(Separator::length))
                .orElseThrow(IllegalStateException::new);
        var iterator = columnSeparators.iterator();
        while (iterator.hasNext()) {
            final PositionSeparators separators = iterator.next();
            separators.removeIf(s -> !s.overlapsWith(separator));
            if (separators.isEmpty())
                iterator.remove();
        }

        iterator = rowSeparators.iterator();
        while (iterator.hasNext()) {
            final PositionSeparators separators = iterator.next();
            if (!separator.contains(separators.pos))
                iterator.remove();
        }
    }

    @Override
    public TableCoordinates resovle(double x, double y) {
        int row = -1;
        for (PositionSeparators rowSeparator : rowSeparators) {
            if (y >= rowSeparator.pos)
                break;
            row++;
        }
        int col = -1;
        for (PositionSeparators separators : columnSeparators) {
            if (x <= separators.pos)
                break;
            col++;
        }
        return new TableCoordinates(row, col);
    }

    private void insertRow(double y, double startX, double width) {
        Separator newSeparator = new RowSeparator(startX, width);
        rowSeparators.insert(y, newSeparator);
    }

    private void insertColumn(double x, double startY, double height) {
        Separator newSeparator = new ColumnSeparator(startY, height);
        columnSeparators.insert(x, newSeparator);
    }

    public PDPage getPage() {
        return page;
    }

    /**
     * accept a tolerance of 2 pixels
     */
    private static int compare(double a, double b) {
        final double d = a - b;
        if (Math.abs(d) <= 2)
            return 0;
        return d < 0 ? -1 : 1;
    }

    public static final class DimensionSeparators implements Iterable<PositionSeparators> {

        private final List<PositionSeparators> separators = new ArrayList<>();
        private final Comparator<Double> positionComparator;

        public DimensionSeparators(Comparator<Double> positionComparator) {
            this.positionComparator = positionComparator;
        }

        public DimensionSeparators(DimensionSeparators from) {
            this.positionComparator = from.positionComparator;
            from.separators.stream().map(PositionSeparators::new).forEach(this.separators::add);
        }

        @Override
        public Iterator<PositionSeparators> iterator() {
            return separators.iterator();
        }

        public boolean isEmpty() {
            return separators.isEmpty();
        }

        public PositionSeparators get(int index) {
            return separators.get(index);
        }

        public PositionSeparators remove(int index) {
            return separators.remove(index);
        }

        private void insert(double pos, Separator separator) {
            PositionSeparators positionSeparators = new PositionSeparators(pos);
            final int index = indexOf(positionSeparators);
            if (index >= 0)
                positionSeparators = separators.get(index);
            else
                separators.add(-index - 1, positionSeparators);
            positionSeparators.insert(separator);
        }

        public int indexOf(PositionSeparators positionSeparators) {
            return Collections.binarySearch(separators, positionSeparators, this::compareSeparators);
        }

        private int compareSeparators(PositionSeparators a, PositionSeparators b) {
            return positionComparator.compare(a.pos, b.pos);
        }

    }

    /**
     * For a given x position, the list of collected {@link PositionSeparators} at this position.
     */
    public static final class PositionSeparators implements Iterable<Separator> {
        private final double pos;
        private final List<Separator> separators = new ArrayList<>();

        public PositionSeparators(double pos) {
            this.pos = pos;
        }

        public PositionSeparators(PositionSeparators other) {
            this.pos = other.pos;
            other.separators.stream().map(Separator::copy).forEach(separators::add);
        }

        public double pos() {
            return pos;
        }

        public int size() {
            return separators.size();
        }

        public void removeIf(Predicate<? super Separator> filter) {
            separators.removeIf(filter);
        }

        @Override
        public Iterator<Separator> iterator() {
            return separators.iterator();
        }

        /**
         * insert a new separator for this position. Expand any  existing separator overlapping this one,
         */
        public void insert(Separator newSeparator) {
            int index = 0;
            for (Separator separator : separators) {
                if (newSeparator.isBefore(separator)) {
                    separators.add(index, separator);
                    return;
                }

                if (newSeparator.equals(separator)) {
                    return;
                }

                if (newSeparator.overlapsWith(separator)) {
                    separator.add(newSeparator);
                    return;
                }

                index++;
            }

            if (index == separators.size()) {
                separators.add(newSeparator);
            }
        }

        public Separator get(int i) {
            return separators.get(i);
        }

        public Stream<Separator> stream() {
            return separators.stream();
        }

        public boolean isEmpty() {
            return separators.isEmpty();
        }
    }

    /**
     * separator along y axis
     */
    public abstract static class Separator {

        public static Separator copy(Separator separator) {
            if (separator instanceof RowSeparator)
                return new RowSeparator((RowSeparator) separator);
            return new ColumnSeparator((ColumnSeparator) separator);
        }

        protected double start;
        protected double length;

        protected Separator(double start, double length) {
            this.start = start;
            this.length = length;
        }

        protected Separator(Separator from) {
            this.start = from.start;
            this.length = from.length;
        }

        public double end() {
            return start + length;
        }

        public double start() {
            return start;
        }

        public double length() {
            return length;
        }

        /**
         * Y axis is bottom -> top, we need to sort separator from top to bottom.
         * This separator is before the other one if it is above.
         */
        public abstract boolean isBefore(Separator separator);

        public boolean isAfter(Separator separator) {
            return separator.isBefore(this);
        }

        public boolean overlapsWith(Separator separator) {
            return !(isBefore(separator) || isAfter(separator));
        }

        public void add(Separator newSeparator) {
            final double newStart = Math.min(start, newSeparator.start);
            final double newEnd = Math.max(end(), newSeparator.end());
            this.start = newStart;
            this.length = newEnd - newStart;
        }

        @Override
        public String toString() {
            return "[" + start + " -> " + end() + "] (" + length + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Separator that = (Separator) o;
            return compare(start, that.start) == 0 && compare(length, that.length) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, length);
        }

        public boolean contains(double pos) {
            return compare(pos, start) >= 0 && compare(pos, end()) <= 0;
        }

    }

    public static final class ColumnSeparator extends Separator {

        public ColumnSeparator(double start, double length) {
            super(start, length);
        }

        public ColumnSeparator(ColumnSeparator from) {
            super(from);
        }

        @Override
        public boolean isBefore(Separator separator) {
            return compare(start, separator.end()) > 0;
        }

    }


    public static final class RowSeparator extends Separator {

        public RowSeparator(double start, double length) {
            super(start, length);
        }

        public RowSeparator(RowSeparator from) {
            super(from);
        }

        @Override
        public boolean isBefore(Separator separator) {
            return compare(end(), separator.start) < 0;
        }

    }
}
