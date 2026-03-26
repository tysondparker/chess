package service;

import dataaccess.MemoryDataAccess;
import dataaccess.exception.*;
import model.requestandresult.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setup() {
        service = new UserService(new MemoryDataAccess());
    }

    @Test
    void registerPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("DonutLarry","Donuts4life","donutluver@gmail.com");
        RegisterResult result = service.register(request);

        assertEquals("DonutLarry", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerNegative() {
        RegisterRequest request = new RegisterRequest("",null,"donutluver@gmail.com");
        assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    void loginPositive() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("DonutLarry","Donuts4life","donutluver@gmail.com");
        service.register(registerRequest);

        LoginRequest request = new LoginRequest("DonutLarry","Donuts4life");
        LoginResult result = service.login(request);

        assertEquals("DonutLarry", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginNegative() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("DonutLarry","Donuts4life","donutluver@gmail.com");
        service.register(registerRequest);

        LoginRequest request = new LoginRequest("DonutLarry","Donuts4lif");
        assertThrows(UnauthorizedException.class, () -> service.login(request));
    }

    @Test
    void logoutPositive() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("DonutLarry", "Donuts4life", "donutluver@gmail.com");
        service.register(registerRequest);

        LoginRequest request = new LoginRequest("DonutLarry","Donuts4life");
        LoginResult result = service.login(request);

        String token = result.authToken();

        LogoutRequest logoutRequest = new LogoutRequest(token);
        service.logout(logoutRequest);

        assertThrows(UnauthorizedException.class, () -> service.logout(new LogoutRequest(token)));
    }

    @Test
    void logoutNegative() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest("DonutLarry", "Donuts4life", "donutluver@gmail.com");
        service.register(registerRequest);

        LoginResult loginResult = service.login(new LoginRequest("DonutLarry", "Donuts4life"));

        LogoutRequest badLogout = new LogoutRequest("thisIsNotARealToken");

        assertThrows(UnauthorizedException.class, () -> service.logout(badLogout));

        assertDoesNotThrow(() -> service.logout(new LogoutRequest(loginResult.authToken())));
    }
}

