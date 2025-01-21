package chess;
import java.util.ArrayList;


public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(position);
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int[][] directionArray = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };
        for (int[] direction : directionArray) {
            // Move in the direction selected by the for loop
            int currentRow = startRow;
            int currentCol = startCol;
            // Continue moving until a barrier or board edge is reached
            while (true) {
                currentRow += direction[0];
                currentCol += direction[1];

                // Adjusted boundary check for 0-indexed arrays
                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                    break;
                }

                ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);
                // if there is another piece at the new position
                if (board.getPiece(currentPosition) != null) {
                    // if it's your same team's color then it's a barrier, break the calculator loop
                    if (movingPiece.getTeamColor() == board.getPiece(currentPosition).getTeamColor()) {
                        break;
                    }
                    // if it's an opposing piece then it's a valid capture move, add it to the ArrayList
                    else {
                        ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                        validMoves.add(potentialMove);

                    }
                }
                // the square is empty and valid, add it to the ArrayList
                else {
                    ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                    validMoves.add(potentialMove);

                }
            }
        }
        return validMoves;
    }
}

