package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "{" + row + ", " + col + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        // Check if both objects are the same reference
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChessPosition other) {
            return row == other.row && col == other.col;
        }
        return false;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }
}
