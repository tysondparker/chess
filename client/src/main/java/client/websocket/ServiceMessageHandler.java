package client.websocket;

import websocket.messages.ServerMessage;

public interface ServiceMessageHandler {
    void notify(ServerMessage message);
}
