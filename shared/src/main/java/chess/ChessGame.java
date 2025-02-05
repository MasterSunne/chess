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
        if (currentBoard.getPiece(startPosition) != null) {
            ChessPiece movingPiece = currentBoard.getPiece(startPosition);
            Collection<ChessMove> pieceMoveList = movingPiece.pieceMoves(currentBoard, startPosition);
            for (ChessMove move : pieceMoveList){
                //copy the current board and make the move
                //check and see if the correct king is now in check
                //if not add to validMoveList
            }
            ArrayList<ChessMove> validMoveList = new ArrayList<>();
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
            ChessPiece movingPiece = currentBoard.getPiece(startPosition);
            if(movingPiece.getTeamColor() == teamTurn){
                ArrayList<ChessMove> validMoveList = (ArrayList<ChessMove>) validMoves(startPosition);
                if (validMoveList.contains(move)){
                    //edit the current board to reflect the valid move
                    //change which team's turn it is
                    if(teamTurn == TeamColor.WHITE){
                        setTeamTurn(TeamColor.BLACK);
                    }else{
                        setTeamTurn(TeamColor.WHITE);
                    }
                }
            }
        } catch (Exception e) {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        // maybe have a move counter that won't worry about checks on the first move
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
