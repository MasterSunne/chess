package chess;

import java.util.ArrayList;

public abstract class MultiSpaceCalculator implements PieceMovesCalculator {
    protected abstract int[][] getDirectionArray();

    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        ChessPiece movingPiece = board.getPiece(position);
        int startRow = position.getRow();
        int startCol = position.getColumn();


        for (int[] direction : getDirectionArray()) {
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

                // if there is another piece at the new position
                if (board.getPiece(currentPosition) != null) {
                    ChessPiece obstaclePiece = board.getPiece(currentPosition);

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
                    ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                    validMoves.add(potentialMove);
                }
            }
        }
        return validMoves;
    }
}
