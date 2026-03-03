package dataaccess;

import dataaccess.exception.DataAccessException;
import model.*;
import service.RequestAndResult.ListGamesRequest;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameId = 0;


    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        users.put(data.username(),data);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String AuthToken) throws DataAccessException {
        return authTokens.get(AuthToken);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        authTokens.clear();
        games.clear();
        gameId = 0;
    }

    @Override
    public void deleteAuth(String authData) throws DataAccessException {
        authTokens.remove(authData);
    }

    @Override
    public int createGame(GameData data) throws DataAccessException {
        gameId += 1;

        GameData newGame = new GameData(gameId,null,null, data.gameName());

        games.put(gameId,newGame);

        return gameId;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        return games.get(gameId);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.remove(game.gameID());
        games.put(game.gameID(),game);
    }

    @Override
    public List<GameData> listGame() throws DataAccessException {
        return new ArrayList<>(games.values());
//        List<GameData> gameList = new ArrayList<>();
//        for (int i = 0; i < games.size(); i++) {
//            GameData data = games.get(i+1);
//            gameList.add(data);
//        }
//        return gameList;
    }
}
