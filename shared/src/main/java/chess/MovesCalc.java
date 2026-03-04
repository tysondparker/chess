package chess;

import java.util.Collection;

public interface MovesCalc {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
