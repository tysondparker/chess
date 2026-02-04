package chess.calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class P_movescalc implements moves_calc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int[] direct = {-1,1};

        ChessPiece mypiece = board.getPiece(myPosition);
        Collection<ChessMove> list_of_moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (mypiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            base checks
            if (row < 7) {
//                forward empty
                if (board.getPiece(new ChessPosition(row+1, col)) == null) {
                    ChessPiece new_tile = board.getPiece(new ChessPosition(row + 1, col));
                    ChessPosition new_position = new ChessPosition(row+1, col);
                    list_of_moves.add(new ChessMove(myPosition, new_position, null));
                } if (row == 2) {
                    if (board.getPiece(new ChessPosition(row+2, col)) == null && board.getPiece(new ChessPosition(row+1, col)) == null) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row + 2, col));
                        ChessPosition new_position = new ChessPosition(row+2, col);
                        list_of_moves.add(new ChessMove(myPosition, new_position, null));
                    }

                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row + 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != mypiece.getTeamColor()) {
                                ChessPosition new_position = new ChessPosition(row +1 , col + direct[j]);
                                list_of_moves.add(new ChessMove(myPosition, new_position, null));
                            }
                        }
                    }
                }
//                row 7
            } else {
                if (board.getPiece(new ChessPosition(row+1, col)) == null) {
                    ChessPiece new_tile = board.getPiece(new ChessPosition(row + 1, col));
                    ChessPosition new_position = new ChessPosition(row+1, col);
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.BISHOP));
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.KNIGHT));
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.QUEEN));
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.ROOK));
                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row + 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != mypiece.getTeamColor()) {
                                ChessPosition new_position = new ChessPosition(row +1 , col + direct[j]);
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.BISHOP));
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.KNIGHT));
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.QUEEN));
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.ROOK));
                            }
                        }
                    }
                }
            }

        } else {
//            Black Pieces
            if (row > 2) {
//                forward empty
                if (board.getPiece(new ChessPosition(row-1, col)) == null) {
                    ChessPiece new_tile = board.getPiece(new ChessPosition(row - 1, col));
                    ChessPosition new_position = new ChessPosition(row-1, col);
                    list_of_moves.add(new ChessMove(myPosition, new_position, null));
                } if (row == 7) {
                    if (board.getPiece(new ChessPosition(row-2, col)) == null && board.getPiece(new ChessPosition(row-1, col)) == null) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row - 2, col));
                        ChessPosition new_position = new ChessPosition(row-2, col);
                        list_of_moves.add(new ChessMove(myPosition, new_position, null));
                    }

                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row - 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != mypiece.getTeamColor()) {
                                ChessPosition new_position = new ChessPosition(row - 1 , col + direct[j]);
                                list_of_moves.add(new ChessMove(myPosition, new_position, null));
                            }
                        }
                    }
                }
//                row 7
            } else {
                if (board.getPiece(new ChessPosition(row-1, col)) == null) {
                    ChessPiece new_tile = board.getPiece(new ChessPosition(row - 1, col));
                    ChessPosition new_position = new ChessPosition(row-1, col);
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.BISHOP));
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.KNIGHT));
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.QUEEN));
                    list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.ROOK));
                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row - 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != mypiece.getTeamColor()) {
                                ChessPosition new_position = new ChessPosition(row - 1 , col + direct[j]);
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.BISHOP));
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.KNIGHT));
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.QUEEN));
                                list_of_moves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.ROOK));
                            }
                        }
                    }
                }
            }

        }
        return list_of_moves;
    }
}
