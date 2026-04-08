package server.websocket;

public class Connection {
    private final int gameId;
    private final String username;

    public int getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }

    public Connection(int gameId, String username) {
        this.gameId = gameId;
        this.username = username;
    }
}
