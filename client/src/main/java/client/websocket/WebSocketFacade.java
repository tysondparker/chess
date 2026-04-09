package client.websocket;
import chess.ChessMove;
import client.WebSocketException;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServiceMessageHandler serviceMessageHandler;

    public WebSocketFacade(String url, ServiceMessageHandler serviceMessageHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serviceMessageHandler = serviceMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            setMessageHandler();
        } catch (Exception ex) {
            throw new WebSocketException("Couldn't connect to Server.");
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connect(String authToken, int gameId) throws WebSocketException {
        System.out.println("Connected to Server");
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex){
            throw new WebSocketException("Sorry champ, I couldn't connect");
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws WebSocketException {
        System.out.println("Inside Make Move");
        try {
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameId, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch(Exception ex) {
            throw new WebSocketException("Sorry, couldn't connect");
        }
    }

    public void resign(String authToken, int gameId) {
        System.out.println("Inside Resign");
        try {
            ResignCommand resignCommand = new ResignCommand(authToken,gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch(Exception ex) {
            throw new WebSocketException("Sorry, couldn't connect");
        }
    }

    public void leave(String authToken, int gameId) {
        System.out.println("Inside leave");
        try {
            LeaveCommand leaveCommand = new LeaveCommand(authToken,gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch(Exception ex) {
            throw new WebSocketException("Sorry, couldn't connect");
        }
    }

    public void setMessageHandler() {
        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            ServerMessage baseMessage = new Gson().fromJson(message, ServerMessage.class);

            switch (baseMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                    serviceMessageHandler.notify(loadGameMessage);
                }
                case NOTIFICATION -> {
                    NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                    serviceMessageHandler.notify(notificationMessage);
                }
                case ERROR -> {
                    ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                    serviceMessageHandler.notify(errorMessage);
                }
            }
        });
    }
}
