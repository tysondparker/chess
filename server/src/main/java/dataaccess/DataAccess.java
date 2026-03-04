package dataaccess;

import dataaccess.exception.DataAccessException;
import model.*;

import java.util.List;

public interface DataAccess {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData data) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    void deleteAuth(String authData) throws DataAccessException;

    int createGame(GameData data) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    void updateGame(GameData gameId) throws DataAccessException;
    List<GameData> listGame() throws DataAccessException;
}
