package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RequestAndResult.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService gameService;
    private UserService userService;

    String registerUser() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("DonutLarry","Donuts4life","donutluver@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("DonutLarry","Donuts4life");
        LoginResult loginResult = userService.login(loginRequest);

        return loginResult.authToken();
    }

    @BeforeEach
    void setup() {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }

    @Test
    void createGamePositive() throws Exception {
        String token = registerUser();

        CreateGameRequest request = new CreateGameRequest("CoolGame");

        CreateGameResult result = gameService.createGame(request,token);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameUnauthorized() {

        CreateGameRequest request = new CreateGameRequest("CoolGame");

        assertThrows(UnauthorizedException.class, () -> gameService.createGame(request, "badToken"));
    }

}
