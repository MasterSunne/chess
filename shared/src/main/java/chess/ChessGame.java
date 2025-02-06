package chess;

import java.lang.reflect.Array;
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
            return validMoveList;
        }
        else{
            return null;
        }
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
            if(getBoard().getPiece(startPosition) == null){
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
                    } else {
                        getBoard().addPiece(endPosition, null);
                        getBoard().addPiece(endPosition,movingPiece);
                        getBoard().addPiece(startPosition,null);
                    }

                    //change which team's turn it is
                    if(getTeamTurn() == TeamColor.WHITE){
                        setTeamTurn(TeamColor.BLACK);
                    }else{
                        setTeamTurn(TeamColor.WHITE);
                    }

                    //check to see if it's checkmate or stalemate and the game should stop, not able to make more moves
                    if(isInCheckmate(getTeamTurn())){
                        boolean checkmate = true;
                        return;
                    } else if (isInStalemate(getTeamTurn())){
                        boolean stalemate = true;
                        return;
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

//    public static void main(String[] args) {
//        ChessGame game = new ChessGame();
//        ChessBoard originalBoard = game.currentBoard;
//        printBoard(originalBoard);
//
//        ChessBoard boardClone = originalBoard.clone();
//
//        printBoard(boardClone);
//    }
//
//    // Helper method to print the board
//    private static void printBoard(ChessBoard board) {
//        System.out.println("Current Board State:");
//        System.out.println(board); // Assumes ChessBoard has a toString() method
//    }
}
