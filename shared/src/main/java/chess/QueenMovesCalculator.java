package chess;
import chess.base.MultiSpaceCalculator;

public class QueenMovesCalculator extends MultiSpaceCalculator implements PieceMovesCalculator {
    private final int[][] directionArray = {
            {1,0},
            {-1,0},
            {0,1},
            {0,-1},
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