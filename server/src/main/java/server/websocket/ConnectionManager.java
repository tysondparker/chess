package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, int gameId, Session session) {
        Connection connection = new Connection(gameId,username);
        connections.put(session, connection);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(int gameId, Session excludeSession, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);

        for (var entry : connections.entrySet()) {
            Session session = entry.getKey();
            Connection connection = entry.getValue();

            if (connection.getGameId() == gameId && !session.equals(excludeSession)) {
                if (session.isOpen()) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }

    public void notify(Session userSession, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);
        if(userSession.isOpen()) {
            userSession.getRemote().sendString(msg);
        }
    }

}