package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class R_movescalc implements moves_calc{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int [][] direct = {{0,1},{1,0},{0,-1},{-1,0}};
        ChessPiece mypiece = board.getPiece(myPosition);
        Collection<ChessMove> list_of_moves = new ArrayList<>();


//        For Loop
        for (int i = 0; i < 4; i++) {

            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while(row + direct[i][0] <= 8 && row + direct[i][0] >= 1 && col + direct[i][1] <= 8 && col + direct[i][1] >= 1) {
                row += direct[i][0];
                col += direct[i][1];
//                no piece there
                ChessPiece new_tile = board.getPiece(new ChessPosition(row,col));
                if(new_tile == null) {
                    ChessPosition new_position = new ChessPosition(row, col);
                    list_of_moves.add(new ChessMove(myPosition,new_position, null));
                }
//                enemy piece there
                else if(new_tile.getTeamColor() != mypiece.getTeamColor()) {
                    ChessPosition new_position = new ChessPosition(row, col);
                    list_of_moves.add(new ChessMove(myPosition,new_position, null));
                    break;
                }
//                our piece there
                else if(new_tile.getTeamColor() == mypiece.getTeamColor()) {
                    break;
                }

            }
        }
        return list_of_moves;
    }
}
