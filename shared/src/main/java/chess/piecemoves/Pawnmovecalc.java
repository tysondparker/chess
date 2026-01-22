package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class Pawnmovecalc implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int [] val = {1,-1};

        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {


            if (position.getRow()+1 == 8) {
                if (board.getPiece(new ChessPosition(position.getRow()+1,position.getColumn())) == null){
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()), ChessPiece.PieceType.BISHOP));
                }
                for (int i = 0; i < 2; i++) {
                    if (board.getPiece(new ChessPosition(position.getRow()+1,position.getColumn()+val[i])) != null && board.getPiece(new ChessPosition(position.getRow()+1,position.getColumn()+val[i])).getTeamColor() != board.getPiece(position).getTeamColor()){
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()+val[i]), ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()+val[i]), ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()+val[i]), ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()+1, position.getColumn()+val[i]), ChessPiece.PieceType.BISHOP));
                    }
                }
            }


            if (board.getPiece(new ChessPosition(position.getRow()+1,position.getColumn())) == null && position.getRow()+1 < 8) {
                if (position.getRow() == 2 && board.getPiece(new ChessPosition(position.getRow() + 2, position.getColumn())) == null) {
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow() + 2, position.getColumn()), null));
                }

                moves.add(new ChessMove(position, new ChessPosition(position.getRow() + 1, position.getColumn()), null));

                for (int i = 0; i < 2; i++) {
                    if (position.getColumn() + val[i] > 0 && position.getColumn() + val[i] < 8 && board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + val[i])) != null && board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + val[i])).getTeamColor() != board.getPiece(position).getTeamColor()) {
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow() + 1, position.getColumn() + val[i]), null));
                    }
                }
            }






        }
        else {
            if (position.getRow()-1 == 1) {
                if (board.getPiece(new ChessPosition(position.getRow()-1,position.getColumn())) == null){
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()), ChessPiece.PieceType.BISHOP));
                }
                for (int i = 0; i < 2; i++) {
                    if (board.getPiece(new ChessPosition(position.getRow()-1,position.getColumn()+val[i])) != null && board.getPiece(new ChessPosition(position.getRow()-1,position.getColumn()+val[i])).getTeamColor() != board.getPiece(position).getTeamColor()){
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()+val[i]), ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()+val[i]), ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()+val[i]), ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow()-1, position.getColumn()+val[i]), ChessPiece.PieceType.BISHOP));
                    }
                }
            }


            if (board.getPiece(new ChessPosition(position.getRow()-1,position.getColumn())) == null && position.getRow()-1 > 1) {
                if (position.getRow() == 7 && board.getPiece(new ChessPosition(position.getRow() - 2, position.getColumn())) == null) {
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow() - 2, position.getColumn()), null));
                }
                moves.add(new ChessMove(position, new ChessPosition(position.getRow() - 1, position.getColumn()), null));
            }
            if (position.getRow()-1 != 1) {
                for (int i = 0; i < 2; i++) {
                    if (position.getColumn() + val[i] > 1 && position.getColumn() + val[i] < 8 && board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + val[i])) != null && board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + val[i])).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        moves.add(new ChessMove(position, new ChessPosition(position.getRow() - 1, position.getColumn() + val[i]), null));
                    }
                }
            }
        }

        return moves;
    }
}