package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnHelper {

    private final ChessBoard board;
    private final ChessPosition myPosition;


    public PawnHelper(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> promotions(Collection<ChessMove> listOfMoves, ChessPosition newPosition) {
        listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
        listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
        listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
        listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
        return listOfMoves;
    }


    public Collection<ChessMove> moveFinder(int rowAdd) {

        Collection<ChessMove> listOfMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece chessPiece = board.getPiece(myPosition);

        int firstMoveRow = 7;
        int lastMoveRow = 2;

        if(rowAdd > 0) {
            firstMoveRow = 2;
            lastMoveRow = 7;
        }

        if (board.getPiece(new ChessPosition(row+rowAdd, col)) == null) {
            board.getPiece(new ChessPosition(row+rowAdd, col));
            ChessPosition newPosition = new ChessPosition(row+rowAdd, col);
            if (row != lastMoveRow) {
                listOfMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                listOfMoves = this.promotions(listOfMoves,newPosition);
            }

        } if (row == firstMoveRow) {
            if (board.getPiece(new ChessPosition(row+(2*rowAdd), col)) == null && board.getPiece(new ChessPosition(row+rowAdd, col)) == null) {
                board.getPiece(new ChessPosition(row+(2*rowAdd), col));
                ChessPosition newPosition = new ChessPosition(row+(2*rowAdd), col);
                listOfMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        if(col -1 >= 1 && col -1 <= 8) {
            ChessPiece newTile = board.getPiece(new ChessPosition(row + rowAdd, col - 1));
            if(newTile != null) {
                if (newTile.getTeamColor() != chessPiece.getTeamColor()) {
                    ChessPosition newPosition = new ChessPosition(row+rowAdd , col-1);
                    if (row != lastMoveRow) {
                        listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        listOfMoves = this.promotions(listOfMoves,newPosition);
                    }
                }
            }
        }

        if(col +1 >= 1 && col +1 <= 8) {
            ChessPiece newTile = board.getPiece(new ChessPosition(row + rowAdd, col + 1));
            if(newTile != null) {
                if (newTile.getTeamColor() != chessPiece.getTeamColor()) {
                    ChessPosition newPosition = new ChessPosition(row+rowAdd , col+1);
                    if (row != lastMoveRow) {
                        listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        listOfMoves = this.promotions(listOfMoves,newPosition);
                    }
                }
            }
        }
        return listOfMoves;
    }
}
