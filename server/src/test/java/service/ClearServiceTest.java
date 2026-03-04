package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RequestAndResult.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private GameService gameService;
    private UserService userService;
    private ClearService clearService;

    String setUp() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("DonutLarry", "Donuts4life", "donutluver@gmail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("DonutLarry", "Donuts4life");
        LoginResult loginResult = userService.login(loginRequest);

        String authToken = loginResult.authToken();

        CreateGameResult game = gameService.createGame(new CreateGameRequest("Valhalla"), authToken);
        gameService.joinGame(new JoinGameRequest("WHITE", game.gameID()), authToken);
        return authToken;
    }

    @BeforeEach
    void setup() {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        clearService = new ClearService(dataAccess);
    }

    @Test
    void clearPositive() throws Exception {
        String authToken = setUp();
        clearService.clear();
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(new ListGamesRequest(authToken)));
    }
}