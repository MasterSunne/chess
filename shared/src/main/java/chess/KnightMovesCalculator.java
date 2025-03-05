package chess;
import java.util.ArrayList;

public class KnightMovesCalculator extends SingleSpaceCalculator implements PieceMovesCalculator {
    private final int[][] directionArray = {
            {1, 2},
            {2, 1},
            {-1, 2},
            {-2, 1},
            {1, -2},
            {2, -1},
            {-1, -2},
            {-2, -1}
    };
    @Override
    protected int[][] getDirectionArray(){
        return directionArray;
    }
}