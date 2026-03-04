package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        int[] direct = {-1,1};

        ChessPiece chessPiece = board.getPiece(myPosition);
        Collection<ChessMove> listOfMoves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            base checks
            if (row < 7) {
//                forward empty
                if (board.getPiece(new ChessPosition(row+1, col)) == null) {
                    board.getPiece(new ChessPosition(row + 1, col));
                    ChessPosition newPosition = new ChessPosition(row+1, col);
                    listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                } if (row == 2) {
                    if (board.getPiece(new ChessPosition(row+2, col)) == null && board.getPiece(new ChessPosition(row+1, col)) == null) {
                        board.getPiece(new ChessPosition(row + 2, col));
                        ChessPosition newPosition = new ChessPosition(row+2, col);
                        listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                    }

                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece newTile = board.getPiece(new ChessPosition(row + 1, col + direct[j]));
                        if (newTile != null) {
                            if (newTile.getTeamColor() != chessPiece.getTeamColor()) {
                                ChessPosition newPosition = new ChessPosition(row +1 , col + direct[j]);
                                listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                        }
                    }
                }
//                row 7
            } else {
                if (board.getPiece(new ChessPosition(row+1, col)) == null) {
                    board.getPiece(new ChessPosition(row + 1, col));
                    ChessPosition new_position = new ChessPosition(row+1, col);
                    listOfMoves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.BISHOP));
                    listOfMoves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.KNIGHT));
                    listOfMoves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.QUEEN));
                    listOfMoves.add(new ChessMove(myPosition, new_position, ChessPiece.PieceType.ROOK));
                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row + 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != chessPiece.getTeamColor()) {
                                ChessPosition newPosition = new ChessPosition(row +1 , col + direct[j]);
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
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
                    board.getPiece(new ChessPosition(row - 1, col));
                    ChessPosition newPosition = new ChessPosition(row-1, col);
                    listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                } if (row == 7) {
                    if (board.getPiece(new ChessPosition(row-2, col)) == null && board.getPiece(new ChessPosition(row-1, col)) == null) {
                        board.getPiece(new ChessPosition(row - 2, col));
                        ChessPosition newPosition = new ChessPosition(row-2, col);
                        listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                    }

                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row - 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != chessPiece.getTeamColor()) {
                                ChessPosition newPosition = new ChessPosition(row - 1 , col + direct[j]);
                                listOfMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                        }
                    }
                }
//                row 7
            } else {
                if (board.getPiece(new ChessPosition(row-1, col)) == null) {
                    board.getPiece(new ChessPosition(row - 1, col));
                    ChessPosition newPosition = new ChessPosition(row-1, col);
                    listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                    listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                }
//                enemy to the side
                for (int j = 0; j < 2; j++) {
                    if (col + direct[j] >= 1 && col + direct[j] <= 8) {
                        ChessPiece new_tile = board.getPiece(new ChessPosition(row - 1, col + direct[j]));
                        if (new_tile != null) {
                            if (new_tile.getTeamColor() != chessPiece.getTeamColor()) {
                                ChessPosition newPosition = new ChessPosition(row - 1 , col + direct[j]);
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                                listOfMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                            }
                        }
                    }
                }
            }

        }
        return listOfMoves;
    }
}
