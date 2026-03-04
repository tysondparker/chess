package service;

import dataaccess.*;
import dataaccess.exception.*;
import model.*;
import service.requestandresult.*;

import static java.util.UUID.randomUUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if ((request.username() == null || request.password() == null || request.email() == null)) {
            throw new BadRequestException("Error: bad request");
        }
        if (dataAccess.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        UserData newUserData = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(newUserData);

        String authToken = randomUUID().toString();
        AuthData newAuthData = new AuthData(authToken,request.username());
        dataAccess.createAuth(newAuthData);

        return new RegisterResult(request.username(),authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (dataAccess.getUser(request.username()) == null || !dataAccess.getUser(request.username()).password().equals(request.password())) {
            throw new UnauthorizedException("Error: bad request");
        }

        UserData user = dataAccess.getUser(request.username());

        String authToken = randomUUID().toString();
        AuthData newAuthData = new AuthData(authToken,user.username());
        dataAccess.createAuth(newAuthData);

        return new LoginResult(request.username(),authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        if (request.authToken() == null || dataAccess.getAuth(request.authToken()) == null) {
            throw new UnauthorizedException("Error: bad request");
        }

        AuthData authData = dataAccess.getAuth(request.authToken());

        dataAccess.deleteAuth(authData.authToken());
        dataAccess.deleteAuth(authData.username());
    }
}
