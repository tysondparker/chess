package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.MovesCalc;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalc implements MovesCalc {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        set everything up
        board.getPiece(myPosition);
        Collection<ChessMove> bishopMoves = new BishopMovesCalc().pieceMoves(board,myPosition);
        Collection<ChessMove> rookMoves = new RookMovesCalc().pieceMoves(board,myPosition);
        Collection<ChessMove> listOfMoves = new ArrayList<>();

        listOfMoves.addAll(bishopMoves);
        listOfMoves.addAll(rookMoves);

        return listOfMoves;
    }
}
