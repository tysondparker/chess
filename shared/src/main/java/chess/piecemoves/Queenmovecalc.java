package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class Queenmovecalc implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(new Bishopmovecalc().pieceMoves(board, position));
        moves.addAll(new Rookmovecalc().pieceMoves(board, position));
        return moves;
    }
}
