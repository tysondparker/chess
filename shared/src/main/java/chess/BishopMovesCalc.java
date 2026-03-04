package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int [][] direct = {{1,1},{1,-1},{-1,-1},{-1,1}};
        ChessPiece chessPiece = board.getPiece(myPosition);
        Collection<ChessMove> listOfMoves = new ArrayList<>();


//        For Loop
        for (int i = 0; i < 4; i++) {

            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while(row + direct[i][0] <= 8 && row + direct[i][0] >= 1 && col + direct[i][1] <= 8 && col + direct[i][1] >= 1) {

                row += direct[i][0];
                col += direct[i][1];

//                no piece there
                ChessPiece newTile = board.getPiece(new ChessPosition(row,col));
                if(newTile == null) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    listOfMoves.add(new ChessMove(myPosition,newPosition, null));
                }
//                enemy piece there
                else if(newTile.getTeamColor() != chessPiece.getTeamColor()) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    listOfMoves.add(new ChessMove(myPosition,newPosition, null));
                    break;
                }
//                our piece there
                else if(newTile.getTeamColor() == chessPiece.getTeamColor()) {
                    break;
                }

            }
        }
        return listOfMoves;
    }
}
