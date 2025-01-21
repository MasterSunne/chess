package chess;
import java.util.ArrayList;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        // Check if the initial position is null
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        System.out.println("Initial position: " + position);

        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(position);
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int[][] directionArray = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        for (int[] direction : directionArray) {
            // Move in the direction selected by the for loop
            int currentRow = startRow;
            int currentCol = startCol;

            currentRow += direction[0];
            currentCol += direction[1];

            // Check boundaries
            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                continue;
            }

            // Create a new ChessPosition and log it
            ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);
            System.out.println("Current position before getting piece: " + currentPosition);

            // Get the piece at the current position and log it
            ChessPiece pieceAtCurrent = board.getPiece(currentPosition);
            if (pieceAtCurrent != null) {
                System.out.println("Piece found at " + currentPosition + ": " + pieceAtCurrent);

                // If it's the same team's piece, stop further movement in this direction
                if (movingPiece.getTeamColor() == pieceAtCurrent.getTeamColor()) {
                    break;
                } else {
                    // Opponent's piece: valid capture move
                    ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                    validMoves.add(potentialMove);
                    break; // Stop further movement after capturing
                }
            } else {
                // Empty square: valid move
                System.out.println("No piece at " + currentPosition);
                ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                validMoves.add(potentialMove);
            }

        }
        return validMoves;

    }
}
