package service;

import dataaccess.*;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UnauthorizedException;
import model.*;
import service.RequestAndResult.*;

import static java.util.UUID.randomUUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if ((request.username() == null || request.password() == null || request.email() == null)) {
            throw new BadRequestException("Error: Missing Information");
        }
        if (dataAccess.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: Username Taken");
        }

        UserData NewUserData = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(NewUserData);

        String authToken = randomUUID().toString();
        AuthData NewAuthData = new AuthData(authToken,request.username());
        dataAccess.createAuth(NewAuthData);

        return new RegisterResult(request.username(),authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: Bad Request");
        }

        if (dataAccess.getUser(request.username()) == null || !dataAccess.getUser(request.username()).password().equals(request.password())) {
            throw new UnauthorizedException("Error: Bad Request");
        }

        UserData user = dataAccess.getUser(request.username());

        String authToken = randomUUID().toString();
        AuthData NewAuthData = new AuthData(authToken,user.username());
        dataAccess.createAuth(NewAuthData);

        return new LoginResult(request.username(),authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        if (request.authToken() == null || dataAccess.getAuth(request.authToken()) == null) {
            throw new UnauthorizedException("Error: Bad Request");
        }

        AuthData authData = dataAccess.getAuth(request.authToken());

        dataAccess.deleteAuth(authData.authToken());
        dataAccess.deleteAuth(authData.username());
    }


}
