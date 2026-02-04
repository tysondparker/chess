package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface moves_calc {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
