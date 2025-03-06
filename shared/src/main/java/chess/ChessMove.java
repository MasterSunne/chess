package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        // Check if both objects are the same reference
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChessMove other) {
            return startPosition.equals(other.startPosition) && endPosition.equals(other.endPosition)
                    && ((promotionPiece == null && other.promotionPiece == null)
                    || (promotionPiece != null && promotionPiece.equals(other.promotionPiece)));}
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition,endPosition,promotionPiece);
    }

    @Override
    public String toString() {
        return startPosition + ", " + endPosition + ", " + promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
