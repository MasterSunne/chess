package chess;
import java.util.ArrayList;

public class PawnMovesCalculator implements PieceMovesCalculator{
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        // Check if the initial position is null
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
//        System.out.println("Initial position: " + position);

        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(position);
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int[][] whiteDirectionArray = {
                {1, 0},
                {1, 1},
                {1, -1},
                // add in functionality for the initial move of 2 spaces
        };
        int[][] blackDirectionArray = {
                {-1, 0},
                {-1, 1},
                {-1, -1},
        };
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
            int currentRow = startRow;
            int currentCol = startCol;

            currentRow += direction[0];
            currentCol += direction[1];

            // Check the boundaries (1-indexed?)
            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                continue;
            }

            // Create the current position object
            ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);
//            System.out.println("Current position before getting piece: " + currentPosition);

            // if there is another piece at the new position
            if (board.getPiece(currentPosition) != null) {
                ChessPiece obstaclePiece = board.getPiece(currentPosition);
//                System.out.println("Piece found at " + currentPosition + ": " + obstaclePiece);

                // if pawn tries to move forward then barrier no matter what
                if (movementTracker < 2){
                    continue;
                }
                // pawn can capture diagonally
                else{
                    ChessPiece victimPiece = board.getPiece(currentPosition);
                    if ((correctDirectionArray == whiteDirectionArray && victimPiece.getTeamColor() == ChessGame.TeamColor.BLACK)|| (correctDirectionArray == blackDirectionArray && victimPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ){
                        if(currentPosition.getRow() == 8 && movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                            ChessMove potentialMove = new ChessMove(position, currentPosition, ChessPiece.PieceType.QUEEN);
                            validMoves.add(potentialMove);
                            ChessMove potentialMove2 = new ChessMove(position, currentPosition, ChessPiece.PieceType.ROOK);
                            validMoves.add(potentialMove2);
                            ChessMove potentialMove3 = new ChessMove(position, currentPosition, ChessPiece.PieceType.BISHOP);
                            validMoves.add(potentialMove3);
                            ChessMove potentialMove4 = new ChessMove(position, currentPosition, ChessPiece.PieceType.KNIGHT);
                            validMoves.add(potentialMove4);
                            continue;
                        }
                        else if(currentPosition.getRow() == 1 && movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                            ChessMove potentialMove = new ChessMove(position, currentPosition, ChessPiece.PieceType.QUEEN);
                            validMoves.add(potentialMove);
                            ChessMove potentialMove2 = new ChessMove(position, currentPosition, ChessPiece.PieceType.ROOK);
                            validMoves.add(potentialMove2);
                            ChessMove potentialMove3 = new ChessMove(position, currentPosition, ChessPiece.PieceType.BISHOP);
                            validMoves.add(potentialMove3);
                            ChessMove potentialMove4 = new ChessMove(position, currentPosition, ChessPiece.PieceType.KNIGHT);
                            validMoves.add(potentialMove4);
                            continue;
                        }
                        ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                        validMoves.add(potentialMove);
                        continue;
                    }
                }
            }
            // the square is empty but only valid if moving forward
            else {
                if (movementTracker < 2){
                    // check for promotion
                    if(currentPosition.getRow() == 8 && movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        ChessMove potentialMove = new ChessMove(position, currentPosition, ChessPiece.PieceType.QUEEN);
                        validMoves.add(potentialMove);
                        ChessMove potentialMove2 = new ChessMove(position, currentPosition, ChessPiece.PieceType.ROOK);
                        validMoves.add(potentialMove2);
                        ChessMove potentialMove3 = new ChessMove(position, currentPosition, ChessPiece.PieceType.BISHOP);
                        validMoves.add(potentialMove3);
                        ChessMove potentialMove4 = new ChessMove(position, currentPosition, ChessPiece.PieceType.KNIGHT);
                        validMoves.add(potentialMove4);
                        continue;
                    }
                    else if(currentPosition.getRow() == 1 && movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                        ChessMove potentialMove = new ChessMove(position, currentPosition, ChessPiece.PieceType.QUEEN);
                        validMoves.add(potentialMove);
                        ChessMove potentialMove2 = new ChessMove(position, currentPosition, ChessPiece.PieceType.ROOK);
                        validMoves.add(potentialMove2);
                        ChessMove potentialMove3 = new ChessMove(position, currentPosition, ChessPiece.PieceType.BISHOP);
                        validMoves.add(potentialMove3);
                        ChessMove potentialMove4 = new ChessMove(position, currentPosition, ChessPiece.PieceType.KNIGHT);
                        validMoves.add(potentialMove4);
                        continue;
                    }

                    ChessMove potentialMove = new ChessMove(position, currentPosition, null);
                    validMoves.add(potentialMove);

                    // next check if the startRow is correct for a double move
                    if(startRow == 2 && movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        currentRow++;
                        ChessPosition doublePosition = new ChessPosition(currentRow, currentCol);
//                        System.out.println("Current position before getting piece: " + doublePosition);

                        // if there is another piece at the new position
                        if (board.getPiece(doublePosition) != null) {
                            ChessPiece obstaclePiece = board.getPiece(doublePosition);
//                            System.out.println("Piece found at " + doublePosition + ": " + obstaclePiece);

                            // if pawn tries to move forward then barrier no matter what
                            if (movementTracker < 2){
                                continue;
                            }
                        }
                        else {
//                            System.out.println("No piece at " + doublePosition);
                            ChessMove doubleMove = new ChessMove(position, doublePosition, null);
                            validMoves.add(doubleMove);}
                    }
                    else if(startRow == 7 && movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                        currentRow--;
                        ChessPosition doublePosition = new ChessPosition(currentRow, currentCol);
//                        System.out.println("Current position before getting piece: " + doublePosition);

                        // if there is another piece at the new position
                        if (board.getPiece(doublePosition) != null) {
                            ChessPiece obstaclePiece = board.getPiece(doublePosition);
//                            System.out.println("Piece found at " + doublePosition + ": " + obstaclePiece);

                            // if pawn tries to move forward then barrier no matter what
                            if (movementTracker < 2){
                                continue;
                            }
                        }
                        else {
//                            System.out.println("No piece at " + doublePosition);
                            ChessMove doubleMove = new ChessMove(position, doublePosition, null);
                            validMoves.add(doubleMove);}
                    }

                }
                else {
                    // can't move diagonal w/o capturing
                    continue;
                }
            }
        }

    return validMoves;
    }
}