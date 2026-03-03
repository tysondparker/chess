package service;

import dataaccess.MemoryDataAccess;
import dataaccess.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RequestAndResult.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService service;

    @BeforeEach
    void setup() {
        service = new GameService(new MemoryDataAccess());
    }

//    @Test
//    void createGamePositive() throws Exception {
//        CreateGameRequest request = new CreateGameRequest("CoolGame");
//
//
//
//        assertNotNull(result);
//        assertTrue(result.gameID() > 0);
//    }

    @Test
    void createGameUnauthorized() {

        CreateGameRequest request = new CreateGameRequest("CoolGame");

        assertThrows(UnauthorizedException.class, () -> service.createGame(request, "badToken"));
    }

}
