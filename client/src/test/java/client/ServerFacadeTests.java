package client;

import model.GameData;
import model.UserData;
import model.requestandresult.*;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws Exception {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTestPos() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        RegisterResult registerResult = serverFacade.register(testUser);

        assertNotNull(registerResult);
        assertEquals("Chica",registerResult.username());
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void registerTestNeg() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica",null,"freddy@gmail.com");
        assertThrows(ClientException.class, () -> {
            serverFacade.register(testUser);
        });
    }

    @Test
    public void loginTestPos() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        serverFacade.register(testUser);

        LoginRequest loginRequest = new LoginRequest("Chica","five nights");
        LoginResult result = serverFacade.login(loginRequest);

        assertNotNull(result);
        assertEquals("Chica",result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginTestNeg() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        serverFacade.register(testUser);

        LoginRequest loginRequest = new LoginRequest("Chica","wrong password");

        assertThrows(ClientException.class, () -> {
            serverFacade.login(loginRequest);
        });
    }

    @Test
    public void logoutTestPos() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        serverFacade.register(testUser);

        LoginRequest loginRequest = new LoginRequest("Chica","five nights");
        LoginResult result = serverFacade.login(loginRequest);

        assertDoesNotThrow(() -> {
            serverFacade.logout(result.authToken());
        });
    }

    @Test
    public void logoutTestNeg() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        serverFacade.register(testUser);

        LoginRequest loginRequest = new LoginRequest("Chica","five nights");
        LoginResult result = serverFacade.login(loginRequest);

        serverFacade.logout(result.authToken());

        assertNotEquals("not auth", result.authToken());
    }

    @Test
    public void listGamePos() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(testUser);

        ListGamesRequest request = new ListGamesRequest(result1.authToken());
        ListGamesResult result = serverFacade.listGame(request);

        assertNotNull(result);
        assertNotNull(result.games());
    }

    @Test
    public void listGameNeg() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(testUser);

        CreateGameRequest request3 = new CreateGameRequest("Five Nights at Freddy's");
        CreateGameResult result3 = serverFacade.createGame(request3,result1.authToken());

        ListGamesRequest request = new ListGamesRequest(result1.authToken());
        ListGamesResult result = serverFacade.listGame(request);


        assertNotNull(result.games().getFirst());
    }

    @Test
    public void createGamePos() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(testUser);

        CreateGameRequest request = new CreateGameRequest("Five Nights at Freddy's");
        CreateGameResult result = serverFacade.createGame(request,result1.authToken());

        assertNotNull(result);
    }

    @Test
    public void createGameNeg() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(testUser);

        CreateGameRequest request = new CreateGameRequest("Five Nights at Freddy's");
        CreateGameResult result = serverFacade.createGame(request,result1.authToken());

        ListGamesRequest request6 = new ListGamesRequest(result1.authToken());
        ListGamesResult result6 = serverFacade.listGame(request6);


        assertNotNull(result6.games().getFirst());
    }


    @Test
    public void joinGamePos() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Chica","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(testUser);

        CreateGameRequest request2 = new CreateGameRequest("Five Nights at Freddy's");
        CreateGameResult result2 = serverFacade.createGame(request2,result1.authToken());

        JoinGameRequest request = new JoinGameRequest("White", result2.gameID());
        serverFacade.joinGame(request,result1.authToken());

        assertDoesNotThrow(() -> {
            serverFacade.joinGame(request,result1.authToken());
        });
    }

    @Test
    public void joinGameNeg() throws Exception {
        RegisterRequest testUser = new RegisterRequest("Bobby","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(testUser);

        CreateGameRequest request2 = new CreateGameRequest("Five Nights at Freddy's");
        CreateGameResult result2 = serverFacade.createGame(request2,result1.authToken());

        JoinGameRequest request = new JoinGameRequest("White", result2.gameID());
        serverFacade.joinGame(request,result1.authToken());

        ListGamesRequest request6 = new ListGamesRequest(result1.authToken());
        ListGamesResult result6 = serverFacade.listGame(request6);

        assert(result6.games().getFirst().whiteUsername() == null);
    }

    @Test
    public void clearPos() throws Exception {
        assertDoesNotThrow(() -> {
            serverFacade.clear();
        });
    }

    @Test
    public void clearNeg() throws Exception {
        RegisterRequest request1 = new RegisterRequest("Bobby","five nights","freddy@gmail.com");
        RegisterResult result1 = serverFacade.register(request1);

        CreateGameRequest request2 = new CreateGameRequest("Five Nights at Freddy's");
        serverFacade.createGame(request2,result1.authToken());

        serverFacade.clear();


        LoginRequest loginRequest = new LoginRequest("bob","wrong password");

        assertThrows(ClientException.class, () -> {
            serverFacade.login(loginRequest);
        });
    }






}
