package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.exception.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RequestAndResult.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService gameService;
    private UserService userService;

    String registerUser() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("DonutLarry", "Donuts4life", "donutluver@gmail.com");
        userService.register(registerRequest);

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
    void createGameNegative() {

        CreateGameRequest request = new CreateGameRequest("CoolGame");

        assertThrows(UnauthorizedException.class, () -> gameService.createGame(request, "badToken"));
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        String authToken = registerUser();

        CreateGameRequest request = new CreateGameRequest("DonutGame");
        CreateGameResult gameResult = gameService.createGame(request,authToken);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", gameResult.gameID());
        assertDoesNotThrow(() -> gameService.joinGame(joinGameRequest, authToken));
    }
    @Test
    void joinGameNegative() throws DataAccessException {
        String authToken = registerUser();

        CreateGameRequest request = new CreateGameRequest("DonutGame");
        CreateGameResult gameResult = gameService.createGame(request,authToken);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", gameResult.gameID());
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(joinGameRequest, "badToken"));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        String authToken = registerUser();

        CreateGameRequest request = new CreateGameRequest("GameOne");
        gameService.createGame(request, authToken);

        List<GameData> result = gameService.listGames(new ListGamesRequest(authToken));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    @Test
    void listGamesNegative(){
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(new ListGamesRequest("badToken")));
    }
}
