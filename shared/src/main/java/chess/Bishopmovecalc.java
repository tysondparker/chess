package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishopmovecalc implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] increments = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};

        for (int i = 0; i < 4; i++) {
            int[] curpos = {position.getRow(), position.getColumn()};
            int[] newpos = {position.getRow() + increments[i][0], position.getColumn() + increments[i][1]};

            while (newpos[0] >= 1 && newpos[0] <= 8 && newpos[1] >= 1 && newpos[1] <= 8) {
                moves.add(new ChessMove(new ChessPosition(curpos[0], curpos[1]), new ChessPosition(newpos[0], newpos[1]), null));
                newpos[0] += increments[i][0];
                newpos[1] += increments[i][1];
            }
        }
        return moves;
    }
}
