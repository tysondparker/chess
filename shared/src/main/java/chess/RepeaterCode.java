package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RepeaterCode {

    private ChessPosition myPosition;
    private int[][] direct;
    private ChessBoard board;
    private ChessPiece chessPiece;

    public RepeaterCode(ChessPosition myPosition, int[][] direct, ChessBoard board, ChessPiece chessPiece) {
        this.myPosition = myPosition;
        this.direct = direct;
        this.board = board;
        this.chessPiece = chessPiece;
    }

    public Collection<ChessMove> returnList() {
    Collection<ChessMove> listOfMoves = new ArrayList<>();

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
