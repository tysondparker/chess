package dataaccess;

import model.*;
import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, String> authTokens = new HashMap<>();

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
        authTokens.put(authData.authToken(),authData.username());
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        authTokens.clear();
    }

    @Override
    public void deleteAuth(String authData) throws DataAccessException {
        authTokens.remove(authData);
    }
}
