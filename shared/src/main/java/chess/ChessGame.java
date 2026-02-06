package chess;

import java.lang.reflect.Type;
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

    private TeamColor current_team = TeamColor.WHITE;
    private ChessBoard current_board = new ChessBoard();

    public ChessGame() {
        current_board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return current_team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        current_team = team;
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
        ChessPiece my_piece = current_board.getPiece(startPosition);
        Collection<ChessMove> final_moves = new ArrayList<>();

        if(my_piece == null) {
            return null;
        }

        Collection<ChessMove> all_moves = my_piece.pieceMoves(current_board,startPosition);
        for (ChessMove move : all_moves) {
            ChessBoard test_board = current_board.deepCopy();
            ChessPiece cur_piece = test_board.getPiece(move.getStartPosition());

            test_board.addPiece(move.getEndPosition(),cur_piece);
            test_board.addPiece(move.getStartPosition(),null);

            ChessGame test_game = new ChessGame();
            test_game.current_board = test_board;

            if (!test_game.isInCheck(my_piece.getTeamColor())){
                final_moves.add(move);
            }
        }
        return final_moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece my_piece = current_board.getPiece(move.getStartPosition());

            ChessPosition start_pos = move.getStartPosition();
            Collection<ChessMove> valid_moves = validMoves(start_pos);

            if (current_board.getPiece(move.getStartPosition()) == null || !valid_moves.contains(move)) {
                throw new InvalidMoveException("Invalid Move Silly Goose");
            }
            if (my_piece.getTeamColor() == current_team) {
                ChessPiece promote = new ChessPiece(my_piece.getTeamColor(), my_piece.getPieceType());

                if (move.getPromotionPiece() != null) {
                    promote = new ChessPiece(my_piece.getTeamColor(), move.getPromotionPiece());
                }

                current_board.addPiece(move.getEndPosition(), promote);
                current_board.addPiece(move.getStartPosition(), null);

                if (my_piece.getTeamColor() != TeamColor.BLACK) {
                    current_team = TeamColor.BLACK;
                } else {
                    current_team = TeamColor.WHITE;
                }
            }
            else {
                throw new InvalidMoveException("Not your turn silly goose");
            }
    }

//    Finds the king
    public Collection<ChessPosition> find_pieces (TeamColor piece_color) {
        Collection<ChessPosition> list_of_pieces = new ArrayList<>();

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition cur_pos = new ChessPosition(i,j);
                if (current_board.getPiece(cur_pos) != null  && current_board.getPiece(cur_pos).getTeamColor() == piece_color){
                    list_of_pieces.add(cur_pos);
                }
            }
        }
        return list_of_pieces;
    }

    public ChessPosition find_piece(Collection<ChessPosition> all_pieces, ChessPiece.PieceType type) {
        for (ChessPosition piece : all_pieces) {
            ChessPiece cur_piece = current_board.getPiece(piece);
            if (cur_piece.getPieceType() == type) {
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
        Collection<ChessPosition> all_pieces = find_pieces(teamColor);
        ChessPosition king_position = find_piece(all_pieces, ChessPiece.PieceType.KING);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition cur_pos = new ChessPosition(i,j);
                ChessPiece cur_piece = current_board.getPiece(cur_pos);
                if (cur_piece != null && cur_piece.getTeamColor() !=  teamColor){
                    Collection<ChessMove> all_cur_piece_moves = cur_piece.pieceMoves(current_board,cur_pos);
                    for (ChessMove move : all_cur_piece_moves) {
                        if (move.getEndPosition().equals(king_position)) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    //    return true if the piece can't make any moves
    public boolean search_piece_moves (Collection<ChessMove> piece_moves, ChessPiece piece, ChessPosition piece_position) {
        for (ChessMove move : piece_moves) {
            ChessBoard test_board = current_board.deepCopy();
            ChessPiece test_piece = test_board.getPiece(piece_position);

            test_board.addPiece(move.getEndPosition(),test_piece);
            test_board.addPiece(move.getStartPosition(),null);

            ChessGame test_game = new ChessGame();
            test_game.current_board = test_board;

            if (!test_game.isInCheck(piece.getTeamColor())) {
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
    public boolean i_need_a_hero(TeamColor teamcolor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition cur_pos = new ChessPosition(i,j);
                ChessPiece cur_piece = current_board.getPiece(cur_pos);
                if(cur_piece != null && cur_piece.getTeamColor() == teamcolor) {
                    Collection<ChessMove> piece_moves = cur_piece.pieceMoves(current_board, cur_pos);
                    for (ChessMove move : piece_moves) {
                        ChessPiece end_tile = current_board.getPiece(move.getEndPosition());
                        if(end_tile != null && end_tile.getTeamColor() != teamcolor) {
                            ChessBoard test_board = current_board.deepCopy();
                            ChessPiece test_piece = test_board.getPiece(cur_pos);

                            test_board.addPiece(move.getEndPosition(), test_piece);
                            test_board.addPiece(move.getStartPosition(), null);

                            ChessGame test_game = new ChessGame();
                            test_game.current_board = test_board;

                            if (!test_game.isInCheck(teamcolor)) {
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
        if (i_need_a_hero(teamColor)) {
            return false;
        }

        Collection<ChessPosition> all_pieces = find_pieces(teamColor);
        ChessPosition king_position = find_piece(all_pieces, ChessPiece.PieceType.KING);
        ChessPiece king_piece = current_board.getPiece(king_position);
        Collection<ChessMove> all_possible_king_moves = king_piece.pieceMoves(current_board,king_position);

        if (!search_piece_moves(all_possible_king_moves, king_piece, king_position)) {
            return false;
        }
        return true;
//
//            for(ChessPosition piece : all_pieces) {
//                ChessPiece cur_piece = current_board.getPiece(piece);
//                if(cur_piece.getPieceType() != ChessPiece.PieceType.KING){
//                    Collection<ChessMove> all_moves = cur_piece.pieceMoves(current_board,piece);
//                    if(!search_piece_moves(all_moves,cur_piece,piece)) {
//                        return false;
//                    }
//                }
//            }
//        return true;
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

        Collection<ChessPosition> all_pieces = find_pieces(teamColor);
        ChessPosition king_position = find_piece(all_pieces, ChessPiece.PieceType.KING);
        ChessPiece king_piece = current_board.getPiece(king_position);
        Collection<ChessMove> all_possible_king_moves = king_piece.pieceMoves(current_board, king_position);

        if (!search_piece_moves(all_possible_king_moves, king_piece, king_position)) {
            return false;
        }

        for(ChessPosition piece : all_pieces) {
            ChessPiece cur_piece = current_board.getPiece(piece);
            if(cur_piece.getPieceType() != ChessPiece.PieceType.KING){
                Collection<ChessMove> all_moves = cur_piece.pieceMoves(current_board,piece);
                if(!search_piece_moves(all_moves,cur_piece,piece)) {
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
        current_board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return current_board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return current_team == chessGame.current_team && Objects.equals(current_board, chessGame.current_board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current_team, current_board);
    }

    @Override
    public String toString() {
        return String.format("%s",current_board);
    }
}
