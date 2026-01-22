package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class Knightmovecalc implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] increments = {{1, 2}, {2, 1}, {1, -2}, {2, -1}, {-1,-2}, {-2,-1},{-1,2},{-2,1}};

        for (int i = 0; i < 8; i++) {
            int[] curpos = {position.getRow(), position.getColumn()};
            int[] newpos = {position.getRow() + increments[i][0], position.getColumn() + increments[i][1]};

            if(newpos[0] >= 1 && newpos[0] <= 8 && newpos[1] >= 1 && newpos[1] <= 8) {
                if(board.getPiece(new ChessPosition(newpos[0],newpos[1])) == null || board.getPiece(new ChessPosition(newpos[0],newpos[1])).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    moves.add(new ChessMove(new ChessPosition(curpos[0], curpos[1]), new ChessPosition(newpos[0], newpos[1]), null));
                }
            }
        }
        return moves;
    }
}