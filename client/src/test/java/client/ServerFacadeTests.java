package client;

import model.UserData;
import model.requestandresult.*;
import org.junit.jupiter.api.*;
import server.Server;

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
        serverFacade.register(testUser);

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
        serverFacade.login(loginRequest);

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
    }

    @Test
    public void listGamePos() throws Exception {
    }

    @Test
    public void listGameNeg() throws Exception {
    }

    @Test
    public void createGamePos() throws Exception {
    }

    @Test
    public void createGameNeg() throws Exception {
    }

    @Test
    public void joinGamePos() throws Exception {
    }

    @Test
    public void joinGameNeg() throws Exception {
    }

    @Test
    public void clearPos() throws Exception {
    }

    @Test
    public void clearNeg() throws Exception {
    }





}
