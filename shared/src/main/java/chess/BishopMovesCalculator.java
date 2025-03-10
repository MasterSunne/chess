package chess;
import chess.base.MultiSpaceCalculator;


public class BishopMovesCalculator extends MultiSpaceCalculator implements PieceMovesCalculator {
    private final int[][] directionArray = {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };
    @Override
    protected int[][] getDirectionArray(){
        return directionArray;
    }
}

