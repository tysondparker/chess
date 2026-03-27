package client;

import model.requestandresult.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
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
    }

    @Test
    public void registerTestNeg() throws Exception {
    }

    @Test
    public void loginTestPos() throws Exception {
    }

    @Test
    public void loginTestNeg() throws Exception {
    }

    @Test
    public void logoutTestPos() throws Exception {
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
