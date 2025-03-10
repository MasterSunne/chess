package chess;

import java.util.ArrayList;
import java.util.Collection;



public class ChessGameValidMoves {
    private final ChessGame currentGame;

    public ChessGameValidMoves(ChessGame currentGame) {
        this.currentGame = currentGame;
    }

    public void setBoardInternal(ChessBoard board) {
        this.currentGame.setBoardInternal(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.currentGame.getBoard();
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // make sure we aren't calculating for a null square
        if (getBoard().getPiece(startPosition) != null) {
            //initialize some useful local vars
            ChessPiece movingPiece = getBoard().getPiece(startPosition);
            Collection<ChessMove> pieceMoveList = movingPiece.pieceMoves(getBoard(), startPosition);
            ArrayList<ChessMove> validMoveList = new ArrayList<>();

            //iterate through the pieceMoveList to eliminate invalid moves
            for (ChessMove move : pieceMoveList) {
                //copy the current board and test the move on the clone
                ChessBoard clonedBoard = getBoard().clone();
                ChessBoard originalBoard = getBoard();
                setBoardInternal(clonedBoard);
                //edit the cloned board to test the move for check on the King
                if(move.getPromotionPiece() != null){ //change the pawn to promotion piece if valid
                    ChessPiece promotionPiece = new ChessPiece(movingPiece.getTeamColor(),move.getPromotionPiece());
                    getBoard().addPiece(move.getEndPosition(),promotionPiece);
                    getBoard().addPiece(move.getStartPosition(),null);
                } else {
                    getBoard().addPiece(move.getEndPosition(),movingPiece);
                    getBoard().addPiece(move.getStartPosition(),null);
                }
                //check and see if the correct king is now in check
                //if not add to validMoveList
                if(movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    if(! currentGame.isInCheck(ChessGame.TeamColor.WHITE)){
                        validMoveList.add(move);
                    }
                } else if (movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                    if(! currentGame.isInCheck(ChessGame.TeamColor.BLACK)){
                        validMoveList.add(move);
                    }
                }
                setBoardInternal(originalBoard);
            }
            checkValidCastlingMoves(movingPiece, validMoveList);
            //check for en passant moves
            if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN){
                if(currentGame.canEnPassantWhite && movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    //copy the current board and test the move on the clone
                    ChessBoard clonedBoard1 = getBoard().clone();
                    ChessBoard clonedBoard2 = getBoard().clone();
                    ChessBoard originalBoard = getBoard();
                    setBoardInternal(clonedBoard1);
                    int newRow = startPosition.getRow() + 1;
                    int newColPlus = startPosition.getColumn() + 1;
                    int newColMinus = startPosition.getColumn() - 1;
                    ChessPosition leftTry = new ChessPosition(newRow,newColMinus);
                    ChessPosition leftCapture = new ChessPosition(startPosition.getRow(),newColMinus);

                    if(newColMinus >= 1 && getBoard().getPiece(leftCapture) != null
                            && getBoard().getPiece(leftCapture).getPieceType() == ChessPiece.PieceType.PAWN ){
                        enPassantTestWhite(startPosition, movingPiece, validMoveList, clonedBoard1, leftTry, leftCapture);
                    }

                    setBoardInternal(clonedBoard2);
                    ChessPosition rightTry = new ChessPosition(newRow,newColPlus);
                    ChessPosition rightCapture = new ChessPosition(startPosition.getRow(),newColPlus);

                    if(newColPlus <= 8 && getBoard().getPiece(rightCapture) != null
                            && getBoard().getPiece(rightCapture).getPieceType() == ChessPiece.PieceType.PAWN){
                        enPassantTestWhite(startPosition, movingPiece, validMoveList, clonedBoard1, rightTry, rightCapture);
                    }
                    setBoardInternal(originalBoard);
                }else if (currentGame.canEnPassantBlack && movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                    //copy the current board and test the move on the clone
                    ChessBoard clonedBoard1 = getBoard().clone();
                    ChessBoard originalBoard = getBoard();
                    setBoardInternal(clonedBoard1);
                    int newRow = startPosition.getRow() - 1;
                    int newColPlus = startPosition.getColumn() + 1;
                    int newColMinus = startPosition.getColumn() - 1;
                    ChessPosition leftTry = new ChessPosition(newRow,newColMinus);
                    ChessPosition leftCapture = new ChessPosition(startPosition.getRow(),newColMinus);

                    if(newColMinus >= 1 && getBoard().getPiece(leftCapture) != null
                            && getBoard().getPiece(leftCapture).getPieceType() == ChessPiece.PieceType.PAWN){
                        enPassantTestBlack(startPosition, movingPiece, validMoveList, clonedBoard1, leftTry, leftCapture);
                    }

                    ChessBoard clonedBoard2 = originalBoard.clone();
                    setBoardInternal(clonedBoard2);
                    ChessPosition rightTry = new ChessPosition(newRow,newColPlus);
                    ChessPosition rightCapture = new ChessPosition(startPosition.getRow(),newColPlus);

                    if(newColPlus <= 8 && getBoard().getPiece(rightCapture) != null
                            && getBoard().getPiece(rightCapture).getPieceType() == ChessPiece.PieceType.PAWN){
                        enPassantTestBlack(startPosition, movingPiece, validMoveList, clonedBoard1, rightTry, rightCapture);
                    }
                    setBoardInternal(originalBoard);
                }
            }
            return validMoveList;
        }
        else{
            return null;
        }
    }

    private void enPassantTestBlack(ChessPosition startPosition, ChessPiece movingPiece, ArrayList<ChessMove> validMoveList,
                                    ChessBoard clonedBoard1, ChessPosition leftTry, ChessPosition leftCapture) {
        clonedBoard1.addPiece(leftTry,movingPiece);
        clonedBoard1.addPiece(startPosition,null);
        clonedBoard1.addPiece(leftCapture,null);
        if(! currentGame.isInCheck(ChessGame.TeamColor.BLACK)){
            ChessMove leftPassant = new ChessMove(startPosition,leftTry,null);
            validMoveList.add(leftPassant);
        }
    }

    private void enPassantTestWhite(ChessPosition startPosition, ChessPiece movingPiece, ArrayList<ChessMove> validMoveList,
                                    ChessBoard clonedBoard1, ChessPosition leftTry, ChessPosition leftCapture) {
        clonedBoard1.addPiece(leftTry,movingPiece);
        clonedBoard1.addPiece(startPosition,null);
        clonedBoard1.addPiece(leftCapture,null);
        if(! currentGame.isInCheck(ChessGame.TeamColor.WHITE)){
            ChessMove leftPassant = new ChessMove(startPosition,leftTry,null);
            validMoveList.add(leftPassant);
        }
    }

    private void checkValidCastlingMoves(ChessPiece movingPiece, ArrayList<ChessMove> validMoveList) {
        //check for castling moves
        if (movingPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if(movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE && canCastleQueenSide(ChessGame.TeamColor.WHITE)){
                if (currentGame.getQueenSideCastleWhite()) {
                    ChessMove queenWhite = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,3),null);
                    validMoveList.add(queenWhite);
                }
            } else if(movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK && canCastleQueenSide(ChessGame.TeamColor.BLACK)){
                if (currentGame.getQueenSideCastleBlack()) {
                    ChessMove queenBlack = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,3),null);
                    validMoveList.add(queenBlack);
                }
            }
            if(movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE && canCastleKingSide(ChessGame.TeamColor.WHITE)){
                if (currentGame.getKingSideCastleWhite()) {
                    ChessMove kingWhite = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,7),null);
                    validMoveList.add(kingWhite);
                }
            } else if(movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK && canCastleKingSide(ChessGame.TeamColor.BLACK)){
                if (currentGame.getKingSideCastleBlack()) {
                    ChessMove kingBlack = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,7),null);
                    validMoveList.add(kingBlack);
                }
            }
        }
    }

    private boolean canCastleKingSide(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            ChessPosition kingPosition = new ChessPosition(1,5);
            ChessPosition rookPosition = new ChessPosition(1,8);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING
                        && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition oneSix = new ChessPosition(1,6);
                    ChessPosition oneSeven = new ChessPosition(1,7);
                    return canCastleHelperKing(teamColor, kingPosition, oneSix, oneSeven);
                }
            }
        }
        else if(teamColor == ChessGame.TeamColor.BLACK) {
            ChessPosition kingPosition = new ChessPosition(8,5);
            ChessPosition rookPosition = new ChessPosition(8,8);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING
                        && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition eightSix = new ChessPosition(8,6);
                    ChessPosition eightSeven = new ChessPosition(8,7);
                    return canCastleHelperKing(teamColor, kingPosition, eightSix, eightSeven);
                }
            }
        }
        return false;
    }

    private boolean canCastleQueenSide(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            ChessPosition kingPosition = new ChessPosition(1,5);
            ChessPosition rookPosition = new ChessPosition(1,1);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING
                        && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition oneFour = new ChessPosition(1,4);
                    ChessPosition oneThree = new ChessPosition(1,3);
                    ChessPosition oneTwo = new ChessPosition(1,2);
                    return canCastleHelperQueen(teamColor, kingPosition, oneFour, oneThree, oneTwo);
                }
            }
        }
        else if (teamColor == ChessGame.TeamColor.BLACK) {
            ChessPosition kingPosition = new ChessPosition(8,5);
            ChessPosition rookPosition = new ChessPosition(8,1);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING
                        && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition eightFour = new ChessPosition(8,4);
                    ChessPosition eightThree = new ChessPosition(8,3);
                    ChessPosition eightTwo = new ChessPosition(8,2);
                    return canCastleHelperQueen(teamColor, kingPosition, eightFour, eightThree, eightTwo);
                }
            }
        }
        return false;
    }

    private boolean canCastleHelperKing(ChessGame.TeamColor teamColor, ChessPosition kingPosition,
                                        ChessPosition firstCheck, ChessPosition secondCheck) {
        if (getBoard().getPiece(firstCheck) == null && getBoard().getPiece(secondCheck) == null) {

            return canCastleHelperBody(teamColor, kingPosition, firstCheck, secondCheck, null);
        }
        return false;
    }
    private boolean canCastleHelperQueen(ChessGame.TeamColor teamColor, ChessPosition kingPosition,
                                         ChessPosition firstCheck, ChessPosition secondCheck, ChessPosition thirdCheck) {
        if (getBoard().getPiece(firstCheck) == null && getBoard().getPiece(secondCheck) == null && getBoard().getPiece(thirdCheck) == null) {

            return canCastleHelperBody(teamColor, kingPosition, firstCheck, secondCheck, thirdCheck);
        }
        return false;
    }

    private boolean canCastleHelperBody(ChessGame.TeamColor teamColor, ChessPosition kingPosition,
                                        ChessPosition firstCheck, ChessPosition secondCheck, ChessPosition thirdCheck) {
        ChessBoard originalBoard = getBoard();
        //check for immediate check threats
        if (!currentGame.isInCheck(teamColor)) {
            originalBoard = getBoard();
            ChessBoard clonedBoard = originalBoard.clone();
            setBoardInternal(clonedBoard);
            getBoard().addPiece(kingPosition, null);
            getBoard().addPiece(firstCheck, new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            //check first square
            if (!currentGame.isInCheck(teamColor)) {
                ChessBoard clonedBoard2 = originalBoard.clone();
                setBoardInternal(clonedBoard2);
                getBoard().addPiece(secondCheck, new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            }
            //check second square
            if (!currentGame.isInCheck(teamColor)) {
                setBoardInternal(originalBoard);
                return true;
            }
        }
        setBoardInternal(originalBoard);
        return false;
    }

}
