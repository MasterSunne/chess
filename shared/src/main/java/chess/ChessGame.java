package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
    private boolean kingSideCastleWhite = true;
    private boolean kingSideCastleBlack = true;
    private boolean queenSideCastleWhite = true;
    private boolean queenSideCastleBlack = true;
    public boolean canEnPassantWhite = false;
    public boolean canEnPassantBlack = false;

    public boolean whiteCheck = false;
    public boolean blackCheck = false;
    public boolean gameOver = false;

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
        this.queenSideCastleWhite = queenSideCastleWhite;}

    public boolean getQueenSideCastleBlack() {
        return queenSideCastleBlack;
    }

    public void setQueenSideCastleBlack(boolean queenSideCastleBlack) {
        this.queenSideCastleBlack = queenSideCastleBlack;}

    public boolean getGameOver(){return gameOver;}

    public void setGameOver(boolean bool){ this.gameOver = bool;}

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return checkmate == chessGame.checkmate
                && stalemate == chessGame.stalemate
                && getKingSideCastleWhite() == chessGame.getKingSideCastleWhite()
                && getKingSideCastleBlack() == chessGame.getKingSideCastleBlack()
                && getQueenSideCastleWhite() == chessGame.getQueenSideCastleWhite()
                && getQueenSideCastleBlack() == chessGame.getQueenSideCastleBlack()
                && canEnPassantWhite == chessGame.canEnPassantWhite
                && canEnPassantBlack == chessGame.canEnPassantBlack
                && whiteCheck == chessGame.whiteCheck
                && blackCheck == chessGame.blackCheck
                && getGameOver() == chessGame.getGameOver()
                && getTeamTurn() == chessGame.getTeamTurn()
                && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), currentBoard, checkmate, stalemate,
                getKingSideCastleWhite(), getKingSideCastleBlack(),
                getQueenSideCastleWhite(), getQueenSideCastleBlack(),
                canEnPassantWhite, canEnPassantBlack, whiteCheck, blackCheck, getGameOver());
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
                        canEnPassantBlack = false;
                        canEnPassantWhite = false;
                    } // castling handler
                    else if(movingPiece.getPieceType() == ChessPiece.PieceType.KING
                            && Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 1){
                        castlingHandler(move, endPosition, movingPiece, startPosition);
                    }
                    // En Passant trigger
                    else if(movingPiece.getPieceType() == ChessPiece.PieceType.PAWN
                            &&  Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) > 1){
                        enPassantTrigger(movingPiece, endPosition, startPosition);
                    }
                    // En Passant handler
                    else if(enPassantHandler(movingPiece,endPosition,move)){
                        ChessPosition capturePosition = new ChessPosition(startPosition.getRow(),endPosition.getColumn());
                        getBoard().addPiece(startPosition,null);
                        getBoard().addPiece(endPosition,movingPiece);
                        getBoard().addPiece(capturePosition,null);
                    }
                    // normal move handler
                    else {
                        getBoard().addPiece(endPosition, null);
                        getBoard().addPiece(endPosition,movingPiece);
                        getBoard().addPiece(startPosition,null);
                        canEnPassantBlack = false;
                        canEnPassantWhite = false;
                    }
                    disableCastleAbility(movingPiece, startPosition);
                    if(getTeamTurn() == TeamColor.WHITE){
                        // white just moved so black may now be in check
                        blackCheck = isInCheck(TeamColor.BLACK);
                        setTeamTurn(TeamColor.BLACK);
                    }else{
                        // if black just moved then white may be in check
                        whiteCheck = isInCheck(TeamColor.WHITE);
                        setTeamTurn(TeamColor.WHITE);
                    }
                    //check to see if it's checkmate or stalemate and the game should stop, not able to make more moves
                    if(isInCheckmate(getTeamTurn())){
                        boolean checkmate = true;
                        setGameOver(true);
                    } else if (isInStalemate(getTeamTurn())){
                        boolean stalemate = true;
                        setGameOver(true);
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

    private void castlingHandler(ChessMove move, ChessPosition endPosition, ChessPiece movingPiece, ChessPosition startPosition) {
        // king's side
        if (move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == -2) {
            //rook
            ChessPosition rookPosition = new ChessPosition(move.getStartPosition().getRow(),6);
            getBoard().addPiece(rookPosition,new ChessPiece(getTeamTurn(), ChessPiece.PieceType.ROOK));
            getBoard().addPiece(new ChessPosition(move.getStartPosition().getRow(),8),null);
            //king
            getBoard().addPiece(endPosition, movingPiece);
            getBoard().addPiece(startPosition,null);
        } else if(move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == 2){
            //rook
            ChessPosition rookPosition = new ChessPosition(move.getStartPosition().getRow(),4);
            getBoard().addPiece(rookPosition,new ChessPiece(getTeamTurn(), ChessPiece.PieceType.ROOK));
            getBoard().addPiece(new ChessPosition(move.getStartPosition().getRow(),1),null);
            //king
            getBoard().addPiece(endPosition, movingPiece);
            getBoard().addPiece(startPosition,null);
        }
        canEnPassantBlack = false;
        canEnPassantWhite = false;
    }

    private void enPassantTrigger(ChessPiece movingPiece, ChessPosition endPosition, ChessPosition startPosition) {
        if(movingPiece.getTeamColor() == TeamColor.WHITE){
            canEnPassantBlack = true;
        } else{
            canEnPassantWhite = true;
        }
        getBoard().addPiece(endPosition, null);
        getBoard().addPiece(endPosition, movingPiece);
        getBoard().addPiece(startPosition,null);
    }

    private boolean enPassantHandler(ChessPiece movingPiece, ChessPosition endPosition, ChessMove move){
        return movingPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && getBoard().getPiece(endPosition) == null
                && ((move.getEndPosition().getRow() == move.getStartPosition().getRow() + 1
                && move.getEndPosition().getColumn() == move.getStartPosition().getColumn() + 1)
                || (move.getEndPosition().getRow() == move.getStartPosition().getRow() + 1
                && move.getEndPosition().getColumn() == move.getStartPosition().getColumn() - 1)
                || (move.getEndPosition().getRow() == move.getStartPosition().getRow() - 1
                && move.getEndPosition().getColumn() == move.getStartPosition().getColumn() + 1)
                || (move.getEndPosition().getRow() == move.getStartPosition().getRow() - 1
                && move.getEndPosition().getColumn() == move.getStartPosition().getColumn() - 1));
    }

    private void disableCastleAbility(ChessPiece movingPiece, ChessPosition startPosition) {
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
            if(getTeamTurn() == TeamColor.WHITE){
                if(startPosition.getRow() == 1 && startPosition.getColumn() == 1){
                    setQueenSideCastleWhite(false);
                } else if(startPosition.getRow() == 1 && startPosition.getColumn() == 8){
                    setKingSideCastleWhite(false);
                }
            }
            else {
                if (startPosition.getRow() == 8 && startPosition.getColumn() == 1) {
                    setQueenSideCastleBlack(false);
                } else if (startPosition.getRow() == 8 && startPosition.getColumn() == 8) {
                    setKingSideCastleBlack(false);
                }
            }
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
                    if (foundPiece.getTeamColor() == teamColor && foundPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        //found the king, save his location
                        kingLocation = scanningPosition;
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
                    if(teamColor.equals(TeamColor.WHITE)){
                        whiteCheck = true;
                    } else{
                        blackCheck = true;
                    }
                    return true;
                }
            }
        }
        if(teamColor.equals(TeamColor.WHITE)){
            whiteCheck = false;
        } else{
            blackCheck = false;
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
                    if (foundPiece.getTeamColor() == teamColor && (! validMoves(scanningPosition).isEmpty())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition){
        ChessGameValidMoves validMoveCalculator = new ChessGameValidMoves(this);
        return validMoveCalculator.validMoves(startPosition);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        setQueenSideCastleBlack(true);
        setQueenSideCastleWhite(true);
        setKingSideCastleBlack(true);
        setKingSideCastleWhite(true);
        this.currentBoard = board;
    }

    public void setBoardInternal(ChessBoard board) {
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
