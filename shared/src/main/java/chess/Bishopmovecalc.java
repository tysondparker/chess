package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishopmovecalc implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int i = 1; i < 4; i++) {


        }
        while (position.getColumn() > 8 || position.getColumn() < 1 || position.getRow() < 1 || position.getRow() > 8) {

            if (position.getColumn() > 8 || position.getColumn() < 1 || position.getRow() < 1 || position.getRow() > 8) {
                break;
            }
        }
    }

}
