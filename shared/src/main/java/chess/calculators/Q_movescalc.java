package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class Q_movescalc implements moves_calc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        ChessPiece mypiece = board.getPiece(myPosition);
        Collection<ChessMove> B_moves = new B_movescalc().pieceMoves(board,myPosition);
        Collection<ChessMove> R_moves = new R_movescalc().pieceMoves(board,myPosition);
        Collection<ChessMove> list_of_moves = new ArrayList<>();

        list_of_moves.addAll(B_moves);
        list_of_moves.addAll(R_moves);

        return list_of_moves;
    }
}
