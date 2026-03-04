package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeam = TeamColor.WHITE;
    private ChessBoard currentBoard = new ChessBoard();

    public ChessGame() {
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = currentBoard.getPiece(startPosition);
        Collection<ChessMove> finalMoves = new ArrayList<>();

        if(myPiece == null) {
            return null;
        }

        Collection<ChessMove> allMoves = myPiece.pieceMoves(currentBoard,startPosition);
        for (ChessMove move : allMoves) {
            ChessBoard testBoard = currentBoard.deepCopy();
            ChessPiece curPiece = testBoard.getPiece(move.getStartPosition());

            testBoard.addPiece(move.getEndPosition(),curPiece);
            testBoard.addPiece(move.getStartPosition(),null);

            ChessGame testGame = new ChessGame();
            testGame.currentBoard = testBoard;

            if (!testGame.isInCheck(myPiece.getTeamColor())){
                finalMoves.add(move);
            }
        }
        return finalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = currentBoard.getPiece(move.getStartPosition());

            ChessPosition startPos = move.getStartPosition();
            Collection<ChessMove> validMoves = validMoves(startPos);

            if (currentBoard.getPiece(move.getStartPosition()) == null || !validMoves.contains(move)) {
                throw new InvalidMoveException("Invalid Move Silly Goose");
            }
            if (myPiece.getTeamColor() == currentTeam) {
                ChessPiece promote = new ChessPiece(myPiece.getTeamColor(), myPiece.getPieceType());

                if (move.getPromotionPiece() != null) {
                    promote = new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece());
                }

                currentBoard.addPiece(move.getEndPosition(), promote);
                currentBoard.addPiece(move.getStartPosition(), null);

                if (myPiece.getTeamColor() != TeamColor.BLACK) {
                    currentTeam = TeamColor.BLACK;
                } else {
                    currentTeam = TeamColor.WHITE;
                }
            }
            else {
                throw new InvalidMoveException("Not your turn silly goose");
            }
    }

//    Finds the king
    public Collection<ChessPosition> findPieces(TeamColor pieceColor) {
        Collection<ChessPosition> listOfPieces = new ArrayList<>();

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition curPos = new ChessPosition(i,j);
                if (currentBoard.getPiece(curPos) != null  && currentBoard.getPiece(curPos).getTeamColor() == pieceColor){
                    listOfPieces.add(curPos);
                }
            }
        }
        return listOfPieces;
    }

    public ChessPosition findPiece(Collection<ChessPosition> allPieces, ChessPiece.PieceType type) {
        for (ChessPosition piece : allPieces) {
            ChessPiece curPiece = currentBoard.getPiece(piece);
            if (curPiece.getPieceType() == type) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> allPieces = findPieces(teamColor);
        ChessPosition kingPosition = findPiece(allPieces, ChessPiece.PieceType.KING);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPos = new ChessPosition(i,j);
                ChessPiece curPiece = currentBoard.getPiece(curPos);
                if (curPiece != null && curPiece.getTeamColor() !=  teamColor){
                    Collection<ChessMove> allCurPieceMoves = curPiece.pieceMoves(currentBoard,curPos);
                    for (ChessMove move : allCurPieceMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    //    return true if the piece can't make any moves
    public boolean searchPieceMoves(Collection<ChessMove> pieceMoves, ChessPiece piece, ChessPosition piecePosition) {
        for (ChessMove move : pieceMoves) {
            ChessBoard testBoard = currentBoard.deepCopy();
            ChessPiece testPiece = testBoard.getPiece(piecePosition);

            testBoard.addPiece(move.getEndPosition(),testPiece);
            testBoard.addPiece(move.getStartPosition(),null);

            ChessGame testGame = new ChessGame();
            testGame.currentBoard = testBoard;

            if (!testGame.isInCheck(piece.getTeamColor())) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param teamcolor color
     * @return True if there is a hero to save the king
     * We need to take ALL the colored pieces and see if any piece can kill an opposing piece putting the king in check
     * take a piece
     * run it through search king moves
     */
    public boolean iNeedAHero(TeamColor teamcolor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition curPos = new ChessPosition(i,j);
                ChessPiece curPiece = currentBoard.getPiece(curPos);
                if(curPiece != null && curPiece.getTeamColor() == teamcolor) {
                    Collection<ChessMove> piecedMoves = curPiece.pieceMoves(currentBoard, curPos);
                    for (ChessMove move : piecedMoves) {
                        ChessPiece endTile = currentBoard.getPiece(move.getEndPosition());
                        if(endTile != null && endTile.getTeamColor() != teamcolor) {
                            ChessBoard testBoard = currentBoard.deepCopy();
                            ChessPiece testPiece = testBoard.getPiece(curPos);

                            testBoard.addPiece(move.getEndPosition(), testPiece);
                            testBoard.addPiece(move.getStartPosition(), null);

                            ChessGame testGame = new ChessGame();
                            testGame.currentBoard = testBoard;

                            if (!testGame.isInCheck(teamcolor)) {
                                return true;
                            }
                        }
                    }

                }

            }

        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        if (iNeedAHero(teamColor)) {
            return false;
        }

        Collection<ChessPosition> allPieces = findPieces(teamColor);
        ChessPosition kingPosition = findPiece(allPieces, ChessPiece.PieceType.KING);
        ChessPiece kingPiece = currentBoard.getPiece(kingPosition);
        Collection<ChessMove> allPossibleKingMoves = kingPiece.pieceMoves(currentBoard,kingPosition);

        return searchPieceMoves(allPossibleKingMoves, kingPiece, kingPosition);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessPosition> allPieces = findPieces(teamColor);
        ChessPosition kingPosition = findPiece(allPieces, ChessPiece.PieceType.KING);
        ChessPiece kingPiece = currentBoard.getPiece(kingPosition);
        Collection<ChessMove> allPossibleKingMoves = kingPiece.pieceMoves(currentBoard, kingPosition);

        if (!searchPieceMoves(allPossibleKingMoves, kingPiece, kingPosition)) {
            return false;
        }

        for(ChessPosition piece : allPieces) {
            ChessPiece curPiece = currentBoard.getPiece(piece);
            if(curPiece.getPieceType() != ChessPiece.PieceType.KING){
                Collection<ChessMove> allMoves = curPiece.pieceMoves(currentBoard,piece);
                if(!searchPieceMoves(allMoves,curPiece,piece)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, currentBoard);
    }

    @Override
    public String toString() {
        return String.format("%s",currentBoard);
    }
}
