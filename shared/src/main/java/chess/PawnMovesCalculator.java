package chess;
import java.util.ArrayList;

public class PawnMovesCalculator implements PieceMovesCalculator{
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        // Check if the initial position is null
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        int[][] whiteDirectionArray = {
                {1, 0},
                {1, 1},
                {1, -1},
        };
        int[][] blackDirectionArray = {
                {-1, 0},
                {-1, 1},
                {-1, -1},
        };
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(position);
        int startRow = position.getRow();
        int startCol = position.getColumn();

        int[][] correctDirectionArray;
        int movementTracker = 0;
        if (movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            correctDirectionArray = whiteDirectionArray;
        }
        else {
            correctDirectionArray = blackDirectionArray;
        }

        for (int[] direction : correctDirectionArray) {
            movementTracker++;
            // Move in the direction selected by the for loop
            int currentRow = (startRow + direction[0]);
            int currentCol = (startCol + direction[1]);

            // Check the boundaries (1-indexed)
            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                continue;
            }

            // Create the current position object
            ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);

            // if there is another piece at the new position
            if (board.getPiece(currentPosition) != null) {

                // if pawn tries to move forward then barrier no matter what -> pawn can capture diagonally
                if(movementTracker > 1){
                    ChessPiece victimPiece = board.getPiece(currentPosition);
                    if ((correctDirectionArray == whiteDirectionArray && victimPiece.getTeamColor() == ChessGame.TeamColor.BLACK)|| (correctDirectionArray == blackDirectionArray && victimPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ){
                        pawnPromotionHelper(position, currentPosition, movingPiece, validMoves);
                    }
                }
            }
            // the square is empty but only valid if moving forward
            else {
                if (movementTracker < 2){
                    // check for promotion
                    pawnPromotionHelper(position, currentPosition, movingPiece, validMoves);

                    // next check if the startRow is correct for a double move
                    if(startRow == 2 && movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        currentRow++;
                        ChessPosition doublePosition = new ChessPosition(currentRow, currentCol);

                        // if there isn't another piece at the new position
                        if (board.getPiece(doublePosition) == null) {
                            ChessMove doubleMove = new ChessMove(position, doublePosition, null);
                            validMoves.add(doubleMove);}
                    }
                    else if(startRow == 7 && movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                        currentRow--;
                        ChessPosition doublePosition = new ChessPosition(currentRow, currentCol);
                        // if there isn't another piece at the new position
                        if (board.getPiece(doublePosition) == null) {
                            ChessMove doubleMove = new ChessMove(position, doublePosition, null);
                            validMoves.add(doubleMove);}
                    }
                }
                // can't move diagonal w/o capturing
            }
        }
    return validMoves;
    }

    private static void pawnPromotionHelper(ChessPosition position, ChessPosition currentPosition, ChessPiece movingPiece, ArrayList<ChessMove> validMoves) {
        if((currentPosition.getRow() == 8 && movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE)||(currentPosition.getRow() == 1 && movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK)){
            ChessMove potentialMove = new ChessMove(position, currentPosition, ChessPiece.PieceType.QUEEN);
            validMoves.add(potentialMove);
            ChessMove potentialMove2 = new ChessMove(position, currentPosition, ChessPiece.PieceType.ROOK);
            validMoves.add(potentialMove2);
            ChessMove potentialMove3 = new ChessMove(position, currentPosition, ChessPiece.PieceType.BISHOP);
            validMoves.add(potentialMove3);
            ChessMove potentialMove4 = new ChessMove(position, currentPosition, ChessPiece.PieceType.KNIGHT);
            validMoves.add(potentialMove4);
            return;
        }
        ChessMove potentialMove = new ChessMove(position, currentPosition, null);
        validMoves.add(potentialMove);
    }
}