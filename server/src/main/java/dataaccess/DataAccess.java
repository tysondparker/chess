package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData data) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    void deleteAuth(String authData) throws DataAccessException;
}
