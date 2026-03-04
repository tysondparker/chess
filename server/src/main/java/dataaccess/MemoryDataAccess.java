package dataaccess;

import dataaccess.exception.DataAccessException;
import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameId = 0;


    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void createUser(UserData data) {
        users.put(data.username(),data);
    }

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String AuthToken)  {
        return authTokens.get(AuthToken);
    }

    @Override
    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
        gameId = 0;
    }

    @Override
    public void deleteAuth(String authData) {
        authTokens.remove(authData);
    }

    @Override
    public int createGame(GameData data) {
        gameId += 1;

        GameData newGame = new GameData(gameId,null,null, data.gameName());

        games.put(gameId,newGame);

        return gameId;
    }

    @Override
    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    @Override
    public void updateGame(GameData game) {
        games.remove(game.gameID());
        games.put(game.gameID(),game);
    }

    @Override
    public List<GameData> listGame() {
        return new ArrayList<>(games.values());
    }
}
