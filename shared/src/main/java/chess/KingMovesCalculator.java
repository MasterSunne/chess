package chess;
import chess.base.SingleSpaceCalculator;

public class KingMovesCalculator extends SingleSpaceCalculator implements PieceMovesCalculator  {
    private final int[][] directionArray = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},
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
