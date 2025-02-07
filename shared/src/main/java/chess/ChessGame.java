package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    public ChessBoard currentBoard;
    private final boolean checkmate = false;
    private final boolean stalemate = false;

    public boolean getKingSideCastleWhite() {
        return kingSideCastleWhite;
    }

    public void setKingSideCastleWhite(boolean kingSideCastleWhite) {
        this.kingSideCastleWhite = kingSideCastleWhite;
    }

    public boolean getKingSideCastleBlack() {
        return kingSideCastleBlack;
    }

    public void setKingSideCastleBlack(boolean kingSideCastleBlack) {
        this.kingSideCastleBlack = kingSideCastleBlack;
    }

    public boolean getQueenSideCastleWhite() {
        return queenSideCastleWhite;
    }

    public void setQueenSideCastleWhite(boolean queenSideCastleWhite) {
        this.queenSideCastleWhite = queenSideCastleWhite;
    }

    public boolean getQueenSideCastleBlack() {
        return queenSideCastleBlack;
    }

    public void setQueenSideCastleBlack(boolean queenSideCastleBlack) {
        this.queenSideCastleBlack = queenSideCastleBlack;
    }

    private boolean kingSideCastleWhite = true;
    private boolean kingSideCastleBlack = true;
    private boolean queenSideCastleWhite = true;
    private boolean queenSideCastleBlack = true;


    // have a no-argument constructor for default values
    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.currentBoard = new ChessBoard();
        this.currentBoard.resetBoard();
    }
    // maintain one with parameters just in case
    public ChessGame(TeamColor teamTurn, ChessBoard currentBoard) {
        this.teamTurn = teamTurn;
        this.currentBoard = currentBoard;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
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
        if (currentBoard.getPiece(startPosition) != null) {
            //initialize some useful local vars
            ChessPiece movingPiece = currentBoard.getPiece(startPosition);
            Collection<ChessMove> pieceMoveList = movingPiece.pieceMoves(currentBoard, startPosition);
            ArrayList<ChessMove> validMoveList = new ArrayList<>();

            //iterate through the pieceMoveList to eliminate invalid moves
            for (ChessMove move : pieceMoveList) {
                //copy the current board and test the move on the clone
                ChessBoard clonedBoard = currentBoard.clone();
                ChessBoard originalBoard = getBoard();
                setBoard(clonedBoard);
                //edit the cloned board to test the move for check on the King
                if(move.getPromotionPiece() != null){ //change the pawn to promotion piece if valid
                    ChessPiece promotionPiece = new ChessPiece(movingPiece.getTeamColor(),move.getPromotionPiece());
                    currentBoard.addPiece(move.getEndPosition(),promotionPiece);
                    currentBoard.addPiece(move.getStartPosition(),null);
                } else {
                    currentBoard.addPiece(move.getEndPosition(),movingPiece);
                    currentBoard.addPiece(move.getStartPosition(),null);
                }
                //check and see if the correct king is now in check
                //if not add to validMoveList
                if(movingPiece.getTeamColor() == TeamColor.WHITE){
                    if(! isInCheck(TeamColor.WHITE)){
                        validMoveList.add(move);
                    }
                } else if (movingPiece.getTeamColor() == TeamColor.BLACK){
                    if(! isInCheck(TeamColor.BLACK)){
                        validMoveList.add(move);
                    }
                }
                setBoard(originalBoard);
            }
            if (movingPiece.getPieceType() == ChessPiece.PieceType.KING) {
                //check for castling moves
                if(teamTurn == TeamColor.WHITE && canCastleQueenSide(TeamColor.WHITE)){
                    ChessMove queenWhite = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,3),null);
                    validMoveList.add(queenWhite);
                } else if(teamTurn == TeamColor.BLACK && canCastleQueenSide(TeamColor.BLACK)){
                    ChessMove queenBlack = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,3),null);
                    validMoveList.add(queenBlack);
                }
                if(teamTurn == TeamColor.WHITE && canCastleKingSide(TeamColor.WHITE)){
                    ChessMove kingWhite = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,7),null);
                    validMoveList.add(kingWhite);
                } else if(teamTurn == TeamColor.BLACK && canCastleKingSide(TeamColor.BLACK)){
                    ChessMove kingBlack = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,7),null);
                    validMoveList.add(kingBlack);
                }
            }
            return validMoveList;
        }
        else{
            return null;
        }
    }

    private boolean canCastleKingSide(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            ChessPosition kingPosition = new ChessPosition(1,5);
            ChessPosition rookPosition = new ChessPosition(1,8);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition oneSix = new ChessPosition(1,6);
                    ChessPosition oneSeven = new ChessPosition(1,7);
                    return canCastleHelperKing(teamColor, kingPosition, oneSix, oneSeven);
                }
            }
        }
        else if(teamColor == TeamColor.BLACK) {
            ChessPosition kingPosition = new ChessPosition(8,5);
            ChessPosition rookPosition = new ChessPosition(8,8);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition eightSix = new ChessPosition(8,6);
                    ChessPosition eightSeven = new ChessPosition(8,7);
                    return canCastleHelperKing(teamColor, kingPosition, eightSix, eightSeven);
                }
            }
        }
        return false;
    }

    private boolean canCastleQueenSide(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            ChessPosition kingPosition = new ChessPosition(1,5);
            ChessPosition rookPosition = new ChessPosition(1,1);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
                    //make sure there aren't any pieces between them
                    ChessPosition oneFour = new ChessPosition(1,4);
                    ChessPosition oneThree = new ChessPosition(1,3);
                    ChessPosition oneTwo = new ChessPosition(1,2);
                    return canCastleHelperQueen(teamColor, kingPosition, oneFour, oneThree, oneTwo);
                }
            }
        }
        else if (teamColor == TeamColor.BLACK) {
            ChessPosition kingPosition = new ChessPosition(8,5);
            ChessPosition rookPosition = new ChessPosition(8,1);
            //make sure the king and rook are on original squares
            if (getBoard().getPiece(kingPosition) != null && getBoard().getPiece(rookPosition) != null) {
                if(getBoard().getPiece(kingPosition).getPieceType() == ChessPiece.PieceType.KING && getBoard().getPiece(rookPosition).getPieceType() == ChessPiece.PieceType.ROOK){
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

    private boolean canCastleHelperKing(TeamColor teamColor, ChessPosition kingPosition, ChessPosition firstCheck, ChessPosition secondCheck) {
        if(getBoard().getPiece(firstCheck) == null && getBoard().getPiece(secondCheck) == null){

            //check for immediate check threats
            if(! isInCheck(teamColor)){
                ChessBoard originalBoard = getBoard();
                ChessBoard clonedBoard = originalBoard.clone();
                setBoard(clonedBoard);
                getBoard().addPiece(kingPosition,null);
                getBoard().addPiece(firstCheck,new ChessPiece(teamColor, ChessPiece.PieceType.KING));
                //check first square
                if(! isInCheck(teamColor)){
                    ChessBoard clonedBoard2 = originalBoard.clone();
                    setBoard(clonedBoard2);
                    getBoard().addPiece(kingPosition,null);
                    getBoard().addPiece(secondCheck,new ChessPiece(teamColor, ChessPiece.PieceType.KING));
                    //check second square
                    if(! isInCheck(teamColor)){
                        setBoard(originalBoard);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean canCastleHelperQueen(TeamColor teamColor, ChessPosition kingPosition, ChessPosition firstCheck, ChessPosition secondCheck, ChessPosition thirdCheck) {
        if(getBoard().getPiece(firstCheck) == null && getBoard().getPiece(secondCheck) == null && getBoard().getPiece(thirdCheck) == null){

            //check for immediate check threats
            if(! isInCheck(teamColor)){
                ChessBoard originalBoard = getBoard();
                ChessBoard clonedBoard = originalBoard.clone();
                setBoard(clonedBoard);
                getBoard().addPiece(kingPosition,null);
                getBoard().addPiece(firstCheck,new ChessPiece(teamColor, ChessPiece.PieceType.KING));
                //check first square
                if(! isInCheck(teamColor)){
                    ChessBoard clonedBoard2 = originalBoard.clone();
                    setBoard(clonedBoard2);
                    getBoard().addPiece(kingPosition,null);
                    getBoard().addPiece(secondCheck,new ChessPiece(teamColor, ChessPiece.PieceType.KING));
                    //check second square
                    if(! isInCheck(teamColor)){
                        setBoard(originalBoard);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            ChessPosition startPosition = move.getStartPosition();
            if(getBoard().getPiece(startPosition) == null) {
                throw new InvalidMoveException();
            }
            ChessPiece movingPiece = getBoard().getPiece(startPosition);
            if(movingPiece.getTeamColor() == getTeamTurn()){
                ArrayList<ChessMove> validMoveList = (ArrayList<ChessMove>) validMoves(startPosition);
                if (validMoveList.contains(move)){
                    ChessPosition endPosition = move.getEndPosition();
                    //edit the current board to reflect the valid move
                    if(move.getPromotionPiece() != null){ //change the pawn to promotion piece if valid
                        ChessPiece promotionPiece = new ChessPiece(movingPiece.getTeamColor(),move.getPromotionPiece());
                        getBoard().addPiece(endPosition, null);
                        getBoard().addPiece(endPosition,promotionPiece);
                        getBoard().addPiece(startPosition,null);
                    } // castling handler
                    else if(movingPiece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 1){
                        // king's side
                        if (move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == -2) {
                            //rook
                            ChessPosition rookPosition = new ChessPosition(move.getStartPosition().getRow(),6);
                            getBoard().addPiece(rookPosition,new ChessPiece(getTeamTurn(), ChessPiece.PieceType.ROOK));
                            getBoard().addPiece(new ChessPosition(move.getStartPosition().getRow(),8),null);
                            //king
                            getBoard().addPiece(endPosition,movingPiece);
                            getBoard().addPiece(startPosition,null);
                        } else if(move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == 2){
                            //rook
                            ChessPosition rookPosition = new ChessPosition(move.getStartPosition().getRow(),4);
                            getBoard().addPiece(rookPosition,new ChessPiece(getTeamTurn(), ChessPiece.PieceType.ROOK));
                            getBoard().addPiece(new ChessPosition(move.getStartPosition().getRow(),1),null);
                            //king
                            getBoard().addPiece(endPosition,movingPiece);
                            getBoard().addPiece(startPosition,null);
                        }
                    } // normal move handler
                    else {
                        getBoard().addPiece(endPosition, null);
                        getBoard().addPiece(endPosition,movingPiece);
                        getBoard().addPiece(startPosition,null);
                    }
                    //if the piece was a rook or king then disable ability to castle
                    if(movingPiece.getPieceType() == ChessPiece.PieceType.KING){
                        if(getTeamTurn() == TeamColor.WHITE){
                            setKingSideCastleWhite(false);
                            setQueenSideCastleWhite(false);
                        } else{
                            setKingSideCastleBlack(false);
                            setQueenSideCastleBlack(false);
                        }
                    } else if(movingPiece.getPieceType() == ChessPiece.PieceType.ROOK){
                        if(getTeamTurn() == TeamColor.WHITE && (startPosition.getRow() == 1 && startPosition.getColumn() == 1)){
                            setQueenSideCastleWhite(false);
                        } else if(getTeamTurn() == TeamColor.WHITE && (startPosition.getRow() == 1 && startPosition.getColumn() == 8)){
                            setKingSideCastleWhite(false);
                        }
                        else if(getTeamTurn() == TeamColor.BLACK && (startPosition.getRow() == 8 && startPosition.getColumn() == 1)){
                            setQueenSideCastleBlack(false);
                        } else if(getTeamTurn() == TeamColor.BLACK && (startPosition.getRow() == 8 && startPosition.getColumn() == 8)){
                            setKingSideCastleBlack(false);
                        }
                    }
//                    //change which team's turn it is
                    if(getTeamTurn() == TeamColor.WHITE){
                        setTeamTurn(TeamColor.BLACK);
                    }else{
                        setTeamTurn(TeamColor.WHITE);
                    }

                    //check to see if it's checkmate or stalemate and the game should stop, not able to make more moves
                    if(isInCheckmate(getTeamTurn())){
                        boolean checkmate = true;
                        //return;
                    } else if (isInStalemate(getTeamTurn())){
                        boolean stalemate = true;
                        //return;
                    }

                    //check if a king or rook moved and set king or queen side booleans to true or false accordingly
                } else if(validMoveList.isEmpty()){
                    throw new InvalidMoveException("Tried to move when no possible moves");
                } else{
                    throw new InvalidMoveException("The move was not found in validMoveList");
                }
            } else{
                throw new InvalidMoveException("Tried to move enemy piece or on enemy's turn");
            }
        } catch (Exception e) {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //loop through the entire chessboard to find the king in question and a list of black piece locations
        ArrayList<ChessPosition> enemyPositions = new ArrayList<>();
        ChessPosition kingLocation = null;
        for (int row = 8; row >= 1; row--) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition scanningPosition = new ChessPosition(row, column);
                if (currentBoard.getPiece(scanningPosition) != null) {
                    ChessPiece foundPiece = currentBoard.getPiece(scanningPosition);
                    if (foundPiece.getTeamColor() == teamColor) {
                        if (foundPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            //found the king, save his location
                            kingLocation = scanningPosition;
                        }
                    }
                    //enemy piece found, save its location
                    else {
                        enemyPositions.add(scanningPosition);
                    }
                }
            }
        }
        //if the target location is empty then the king doesn't exist, throw error
        if (kingLocation == null) {
            throw new RuntimeException("King not found");
        }
        //scan enemy pieceMoves and see if they threaten the king (same position)
        for(ChessPosition enemyLocation : enemyPositions){
            ChessPiece enemy = currentBoard.getPiece(enemyLocation);
            ArrayList<ChessMove> enemyMoves = (ArrayList<ChessMove>) enemy.pieceMoves(currentBoard, enemyLocation);
            for(ChessMove enemyMove : enemyMoves){
                ChessPosition targetPosition = enemyMove.getEndPosition();
                if (targetPosition.equals(kingLocation)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return noValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (! isInCheck(teamColor)) {
            return noValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if a team has any given moves for checkmate and stalemate use.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team has no more valid moves, otherwise false
     */
    public boolean noValidMoves(TeamColor teamColor) {
        for (int row = 8; row >= 1; row--) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition scanningPosition = new ChessPosition(row, column);
                if (getBoard().getPiece(scanningPosition) != null) {
                    ChessPiece foundPiece = getBoard().getPiece(scanningPosition);
                    if (foundPiece.getTeamColor() == teamColor) {
                        if (! validMoves(scanningPosition).isEmpty()){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
