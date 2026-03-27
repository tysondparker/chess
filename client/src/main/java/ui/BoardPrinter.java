package ui;
import chess.*;

public class BoardPrinter {

    public static void drawBoard(ChessGame game, ChessGame.TeamColor color){
//        Set up the board
        ChessBoard board = game.getBoard();
        int rowStart, rowEnd, rowStep;
        int colStart, colEnd, colStep;
        String[] labels;

        if(color == ChessGame.TeamColor.WHITE){
            rowStart=8; rowEnd=1; rowStep=-1;
            colStart=1; colEnd=8; colStep=1;
            labels = new String[]{"a","b","c","d","e","f","g","h"};
        } else {
            rowStart=1; rowEnd=8; rowStep=1;
            colStart=8; colEnd=1; colStep=-1;
            labels = new String[]{"h","g","f","e","d","c","b","a"};
        }

//        Draw the top left corner
        System.out.print(EscapeSequences.PRINT_CORNER);
        System.out.print(" ☺ ");
        System.out.print(EscapeSequences.RESET_PARAMS);

//        Draw the top letters
        for (String label : labels) {
            System.out.print(EscapeSequences.PRINT_LETTERS);
            System.out.print(" "+label+" ");
            System.out.print(EscapeSequences.RESET_PARAMS);
        }

//        Draw the top right corner
        System.out.print(EscapeSequences.PRINT_CORNER);
        System.out.print(" ☺ ");
        System.out.print(EscapeSequences.RESET_PARAMS);
        System.out.println();

//        Print row and col one at a time
        for(int row = rowStart; true; row += rowStep) {
            printRowLabel(row);
            for (int col = colStart; true; col += colStep) {
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                boolean evenSquare = ((row + col) % 2 == 0);
                makeSquare(piece,evenSquare);
                if (col == colEnd) {
                    break;
                }
            }
            printRowLabel(row);
            System.out.println();
            if (row == rowEnd) {
                break;
            }
        }

//        Print bottom left corner
        System.out.print(EscapeSequences.PRINT_CORNER);
        System.out.print(" ☺ ");
        System.out.print(EscapeSequences.RESET_PARAMS);

//        print bottom letters
        for (String label : labels) {
            System.out.print(EscapeSequences.PRINT_LETTERS);
            System.out.print(" "+label+" ");
            System.out.print(EscapeSequences.RESET_PARAMS);
        }

//        Print bottom right corner
        System.out.print(EscapeSequences.PRINT_CORNER);
        System.out.print(" ☺ ");
        System.out.print(EscapeSequences.RESET_PARAMS);
        System.out.println();

    }
    private static void makeSquare(ChessPiece piece, boolean evenSquare) {
        String backGroundColor;
        if(evenSquare){
            backGroundColor = EscapeSequences.SET_BG_COLOR_WHITE;
        } else {
            backGroundColor = EscapeSequences.SET_BG_COLOR_BLACK;
        }

        System.out.print(backGroundColor);
        System.out.print(pieceString(piece));
    }

    private static String pieceString(ChessPiece piece){
        if(piece == null) {
            return EscapeSequences.EMPTY;
        }

        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        }
    }

    private static void printRowLabel(int row) {
        System.out.print(EscapeSequences.PRINT_LETTERS);
        System.out.print(" "+row+" ");
        System.out.print(EscapeSequences.RESET_PARAMS);
    }
}
