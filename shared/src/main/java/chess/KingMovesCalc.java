package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int [][] direct = {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,-1},{-1,1}};
        ChessPiece mypiece = board.getPiece(myPosition);
        Collection<ChessMove> list_of_moves = new ArrayList<>();


//        For Loop
        for (int i = 0; i < 8; i++) {

            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            if (row + direct[i][0] <= 8 && row + direct[i][0] >= 1 && col + direct[i][1] <= 8 && col + direct[i][1] >= 1) {
                row += direct[i][0];
                col += direct[i][1];
//                no piece there
                ChessPiece newTile = board.getPiece(new ChessPosition(row,col));
                if(newTile == null) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    list_of_moves.add(new ChessMove(myPosition,newPosition, null));
                }
//                enemy piece there
                else if(newTile.getTeamColor() != mypiece.getTeamColor()) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    list_of_moves.add(new ChessMove(myPosition,newPosition, null));
                }
            }
        }
        return list_of_moves;
    }
}
