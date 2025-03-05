package chess;
import java.util.ArrayList;


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

