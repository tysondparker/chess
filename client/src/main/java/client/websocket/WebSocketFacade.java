package client.websocket;
import chess.ChessMove;
import client.WebSocketException;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.*;
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

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage baseMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (baseMessage.getServerMessageType()) {
                        case LOAD_GAME -> serviceMessageHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                        case NOTIFICATION -> serviceMessageHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                        case ERROR -> serviceMessageHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                    }
                }
            });
        } catch (Exception ex) {
            throw new WebSocketException("Couldn't connect to Server.\n");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
//        setMessageHandler();
    }

    public void connect(String authToken, int gameId) throws WebSocketException {
        try {
            ConnectCommand connectCommand = new ConnectCommand(authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException ex){
            throw new WebSocketException("Sorry champ, I couldn't connect");
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws WebSocketException {
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
        try {
            LeaveCommand leaveCommand = new LeaveCommand(authToken,gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch(Exception ex) {
            throw new WebSocketException("Sorry, couldn't connect");
        }
    }
}
