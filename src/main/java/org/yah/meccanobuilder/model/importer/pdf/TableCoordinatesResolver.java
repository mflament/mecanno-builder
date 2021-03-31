package org.yah.meccanobuilder.model.importer.pdf;

import java.util.Objects;

public interface TableCoordinatesResolver {

    TableCoordinates resovle(double x, double y);

    final class TableCoordinates {
        public final int row;
        public final int col;

        public TableCoordinates(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TableCoordinates that = (TableCoordinates) o;
            return row == that.row && col == that.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return "(" + row + ", " + col + ")";
        }
    }
}
