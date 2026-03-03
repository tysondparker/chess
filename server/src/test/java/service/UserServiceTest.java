package service;

import dataaccess.MemoryDataAccess;
import dataaccess.exception.*;
import org.junit.jupiter.api.*;
import service.RequestAndResult.RegisterRequest;
import service.RequestAndResult.RegisterResult;

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
    void registerNegative() throws Exception {
        RegisterRequest request = new RegisterRequest("",null,"donutluver@gmail.com");
        assertThrows(BadRequestException.class, () -> service.register(request));
    }
}

