package chess;


import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece [][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1 ] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8) {
            return null;
        }
        return squares[position.getRow()-1][position.getColumn()-1 ];
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (row == 2) {
                    ChessPiece Pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    this.addPiece(new ChessPosition(row,col),Pawn);
                }
                if (row == 7) {
                    ChessPiece Pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    this.addPiece(new ChessPosition(row,col),Pawn);
                }


                if (row == 8 && col == 1 || row == 8 && col == 8) {
                    ChessPiece Rook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    this.addPiece(new ChessPosition(row,col),Rook);
                }
                if (row == 1 && col == 1 || row == 1 && col == 8) {
                    ChessPiece Rook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    this.addPiece(new ChessPosition(row,col),Rook);
                }


                if (row == 8 && col == 2 || row == 8 && col == 7) {
                    ChessPiece n = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    this.addPiece(new ChessPosition(row,col),n);
                }
                if (row == 1 && col == 2 || row == 1 && col == 7) {
                    ChessPiece n = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    this.addPiece(new ChessPosition(row,col),n);
                }


                if (row == 8 && col == 3 || row == 8 && col == 6) {
                    ChessPiece b = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    this.addPiece(new ChessPosition(row,col),b);
                }
                if (row == 1 && col == 3 || row == 1 && col == 6) {
                    ChessPiece b = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    this.addPiece(new ChessPosition(row,col),b);
                }


                if (row == 8 && col == 4) {
                    ChessPiece q = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                    this.addPiece(new ChessPosition(row,col),q);
                }
                if (row == 1 && col == 4) {
                    ChessPiece q = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                    this.addPiece(new ChessPosition(row,col),q);
                }

                if (row == 8 && col == 5) {
                    ChessPiece k = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                    this.addPiece(new ChessPosition(row,col),k);
                }
                if (row == 1 && col == 5) {
                    ChessPiece k = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                    this.addPiece(new ChessPosition(row,col),k);
                }


            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
