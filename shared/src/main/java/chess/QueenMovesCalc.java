package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        board.getPiece(myPosition);
        Collection<ChessMove> B_moves = new BishopMovesCalc().pieceMoves(board,myPosition);
        Collection<ChessMove> R_moves = new RookMovesCalc().pieceMoves(board,myPosition);
        Collection<ChessMove> list_of_moves = new ArrayList<>();

        list_of_moves.addAll(B_moves);
        list_of_moves.addAll(R_moves);

        return list_of_moves;
    }
}
