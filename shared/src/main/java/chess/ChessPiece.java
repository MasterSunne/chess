package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public final class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING(new KingMovesCalculator()),
        QUEEN(new QueenMovesCalculator()),
        BISHOP(new BishopMovesCalculator()),
        KNIGHT(new KnightMovesCalculator()),
        ROOK(new RookMovesCalculator()),
        PAWN(new PawnMovesCalculator());

        private final PieceMovesCalculator moveCalculator;

        PieceType(PieceMovesCalculator moveCalculator) {
            this.moveCalculator = moveCalculator;
        }

        public PieceMovesCalculator getMoveCalculator() {
            return moveCalculator;
        }
    }

    @Override
    public String toString() {
        return pieceColor + " " + type.toString();
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return type.getMoveCalculator().pieceMoves(board, myPosition);
    }
}
