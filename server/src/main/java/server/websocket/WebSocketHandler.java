package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.exception.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import io.javalin.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Map;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand, ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(makeMoveCommand, ctx.session);
                }
                case RESIGN -> resign(userGameCommand.getAuthToken(), ctx.session);
                case LEAVE -> leave(userGameCommand.getAuthToken(), ctx.session);
            }
        } catch (Exception ex) {
            try {
                connections.notify(ctx.session, new ErrorMessage("Error: " + ex.getMessage()));
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        AuthData authData = dataAccess.getAuth(command.getAuthToken());

        if(authData == null) {
            connections.notify(session, new ErrorMessage("Error: Unauthorized"));
            return;
        }

        GameData gameData = dataAccess.getGame(command.getGameID());

        if(gameData == null) {
            connections.notify(session, new ErrorMessage("Error: Bad game ID"));
            return;
        }

        String username = authData.username();

        connections.add(username, command.getGameID(), session);

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        connections.notify(session, loadGameMessage);

        String note;
        if (authData.username().equals(gameData.whiteUsername())) {
            note = username + " joined game as white.";
        } else if (authData.username().equals(gameData.blackUsername())) {
            note = username + " joined game as black.";
        } else {
            note = username + " joined as an observer";
        }

        connections.broadcast(gameData.gameID(), session, new NotificationMessage(note));
    }

    private void makeMove(MakeMoveCommand command, Session session) throws Exception {

        AuthData authData = dataAccess.getAuth(command.getAuthToken());
        if(authData == null) {
            connections.notify(session, new ErrorMessage("Couldn't connect"));
            return;
        }

        GameData gameData = dataAccess.getGame(command.getGameID());
        if(gameData == null) {
            connections.notify(session, new ErrorMessage("Couldn't connect"));
            return;
        }

        ChessGame game = gameData.game();

        String username = authData.username();

        ChessGame.TeamColor playerColor;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            connections.notify(session, new ErrorMessage("If you're observing, you can't play. Sorry"));
            return;
        }

        if(!game.getTeamTurn().equals(playerColor)) {
            connections.notify(session, new ErrorMessage("Sorry, it's not your turn, wait for your opponent"));
            return;
        }

        ChessMove userMove = command.getMove();

        try{
            game.makeMove(userMove);
        } catch (InvalidMoveException e) {
            connections.notify(session, new ErrorMessage("Hey, that's not an approved move silly"));
            return;
        }

        GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        dataAccess.updateGame(updatedGame);

        connections.broadcast(updatedGame.gameID(), null, new LoadGameMessage(game));

        Map<Integer, String> numbermap = Map.of(
                1,"A", 2, "B", 3, "C", 4,
                "D", 5, "E", 6, "F", 7,
                "G", 8, "H"
        );

        int startCol = command.getMove().getStartPosition().getColumn();
        String startRow = numbermap.get(command.getMove().getStartPosition().getRow());

        int endCol = command.getMove().getEndPosition().getColumn();
        String endRow = numbermap.get(command.getMove().getEndPosition().getRow());

        NotificationMessage notificationMessage = new NotificationMessage(String.format("%s moved %s%d to %s%d",username,startRow,startCol,endRow,endCol));
        connections.broadcast(updatedGame.gameID(),session,notificationMessage);

        ChessGame.TeamColor opponentColor = ChessGame.TeamColor.WHITE;
        NotificationMessage message;

        if(playerColor == ChessGame.TeamColor.WHITE) {
            opponentColor = ChessGame.TeamColor.BLACK;
        }

        if(game.isInCheck(opponentColor)) {
            if(game.isInCheckmate(opponentColor)) {
                message = new NotificationMessage(username+ " has put you in checkmate! Game over");
                connections.broadcast(gameData.gameID(), session,message);
                return;
            }
            message = new NotificationMessage(username+ " has put you in check!");
            connections.broadcast(gameData.gameID(), session,message);
        } else if (game.isInStalemate(opponentColor)) {
            message = new NotificationMessage("You're in stalemate! Game over");
            connections.broadcast(gameData.gameID(), session,message);
        }
    }

    private void resign(String authToken, Session session) {

    }

    private void leave(String authToken, Session session) {

    }
}
