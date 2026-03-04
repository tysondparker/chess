package chess.calculators;

import chess.*;

import java.util.Collection;

public class RookMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int [][] direct = {{0,1},{1,0},{0,-1},{-1,0}};
        ChessPiece chessPiece = board.getPiece(myPosition);
        RepeaterCode repeaterCode = new RepeaterCode(myPosition,direct,board,chessPiece);
        return repeaterCode.returnList();
    }
}
