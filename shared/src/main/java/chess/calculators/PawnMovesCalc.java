package chess.calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int[] direct = {-1,1};
        PawnHelper pawnHelper = new PawnHelper(board,myPosition);
        Collection<ChessMove> listOfMoves = new ArrayList<>();

        ChessPiece chessPiece = board.getPiece(myPosition);

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            listOfMoves = pawnHelper.moveFinder(1);
        } else {
            listOfMoves = pawnHelper.moveFinder(-1);
        }
        return listOfMoves;
    }
}
