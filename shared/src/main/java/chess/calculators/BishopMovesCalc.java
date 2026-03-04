package chess.calculators;

import chess.*;

import java.util.Collection;

public class BishopMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int [][] direct = {{1,1},{1,-1},{-1,-1},{-1,1}};
        ChessPiece chessPiece = board.getPiece(myPosition);
        RepeaterCode repeaterCode = new RepeaterCode(myPosition,direct,board,chessPiece);
        return repeaterCode.returnList();
    }
}
