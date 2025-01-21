package chess;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(position);
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int[][] directionArray = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };

        for (int[] direction : directionArray) {
            // Move in the direction selected by the for loop
            int currentRow = startRow;
            int currentCol = startCol;

            // Keep moving until a barrier or board edge is reached
            while (true) {
                currentRow += direction[0];
                currentCol += direction[1];

                // Check the boundaries (1-indexed?)
                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                    break;
                }

                // Create the current position object
                ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);
//                System.out.println("Current position before getting piece: " + currentPosition);

                // if there is another piece at the new position
                if (board.getPiece(currentPosition) != null) {
                    ChessPiece obstaclePiece = board.getPiece(currentPosition);
//                    System.out.println("Piece found at " + currentPosition + ": " + obstaclePiece);

                    // if it's your same team's color then it's a barrier, break the calculator loop
                    if (movingPiece.getTeamColor() == obstaclePiece.getTeamColor()) {
                        break;
                    }
                    // if it's an opposing piece then it's a valid capture move, add it to the ArrayList
                    else {
                        ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                        validMoves.add(potentialMove);
                        break;
                    }
                }
                // the square is empty and valid, add it to the ArrayList
                else {
//                    System.out.println("No piece at " + currentPosition);
                    ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                    validMoves.add(potentialMove);
                }
            }
        }
        return validMoves;
    }
}