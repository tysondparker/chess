package dataaccess;

import dataaccess.exception.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public class SQLDataAccess implements DataAccess {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authData) throws DataAccessException {

    }

    @Override
    public int createGame(GameData data) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameId) throws DataAccessException {

    }

    @Override
    public List<GameData> listGame() throws DataAccessException {
        return List.of();
    }
}
