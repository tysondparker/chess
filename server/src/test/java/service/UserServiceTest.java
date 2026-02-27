package service;

import dataaccess.MemoryDataAccess;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Positive + negative tests for UserService.
 * The tests call the service methods directly (no HTTP server).
 */
public class UserServiceTest {

    private DataAccess dao;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // fresh in-memory DAO for each test to avoid state leakage
        dao = new MemoryDataAccess();
        userService = new UserService(dao);
    }

    // ---------- register() positive ----------
    @Test
    public void register_success_createsUserAndReturnsToken() throws Exception {
        var req = new RegisterRequest("alice", "password123", "a@b.com");
        RegisterResult res = userService.register(req);

        assertNotNull(res);
        assertEquals("alice", res.username());
        assertNotNull(res.authToken());
        // token should be stored in DAO
        AuthData stored = dao.getAuth(res.authToken());
        assertNotNull(stored);
        assertEquals("alice", stored.username());
        // user should be stored
        UserData u = dao.getUser("alice");
        assertNotNull(u);
        assertEquals("a@b.com", u.email());
    }

    // ---------- register() negative: duplicate username ----------
    @Test
    public void register_duplicateUsername_throwsServiceException() throws Exception {
        // first registration
        var req1 = new RegisterRequest("bob", "pw", "bob@x.com");
        userService.register(req1);

        // second registration with same username should fail
        var req2 = new RegisterRequest("bob", "other", "bob2@x.com");
        ServiceException ex = assertThrows(ServiceException.class, () -> userService.register(req2));
        assertEquals(403, ex.status); // matches spec: already taken
    }

    // ---------- login() positive ----------
    @Test
    public void login_success_returnsToken() throws Exception {
        // prepare: insert a user directly into DAO
        dao.createUser(new UserData("carol", "secret", "c@c.com"));

        var loginReq = new LoginRequest("carol", "secret");
        LoginResult res = userService.login(loginReq);

        assertNotNull(res);
        assertEquals("carol", res.username());
        assertNotNull(res.authToken());
        // auth stored
        AuthData auth = dao.getAuth(res.authToken());
        assertNotNull(auth);
        assertEquals("carol", auth.username());
    }

    // ---------- login() negative: bad password ----------
    @Test
    public void login_badPassword_throwsUnauthorized() throws Exception {
        dao.createUser(new UserData("dave", "rightpw", "d@d.com"));

        var loginReq = new LoginRequest("dave", "wrongpw");
        ServiceException ex = assertThrows(ServiceException.class, () -> userService.login(loginReq));
        assertEquals(401, ex.status);
    }

    // ---------- logout() positive ----------
    @Test
    public void logout_success_deletesAuth() throws Exception {
        // prepare: create auth token and store it
        AuthData auth = new AuthData("token-1", "eve");
        dao.createAuth(auth);

        var logoutReq = new LogoutRequest("token-1");
        userService.logout(logoutReq);

        assertNull(dao.getAuth("token-1"));
    }

    // ---------- logout() negative: unknown token ----------
    @Test
    public void logout_unknownToken_throwsUnauthorized() {
        var logoutReq = new LogoutRequest("no-such-token");
        ServiceException ex = assertThrows(ServiceException.class, () -> userService.logout(logoutReq));
        assertEquals(401, ex.status);
    }
}