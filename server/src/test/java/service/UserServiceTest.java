package service;

import request.*;
import result.*;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    // Declare variables as instance (global) variables
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService uService;

    @BeforeEach
    void initialization() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        uService = new UserService(authDAO, userDAO);
    }

    @Test
    void testRegisterSuccess() throws DataAccessException {
        // if
        RegisterRequest request = new RegisterRequest("testUser", "password", "testUser@test.com");

        // when
        RegLogResult result = uService.register(request);

        // then
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull( result.authToken());
    }
    @Test
    // no email
    void testRegisterFailure() throws DataAccessException {
        // if
        RegisterRequest request = new RegisterRequest("testUser", "password", null);


        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            uService.register(request);
        });

        assertEquals(400, exception.statusCode());
        assertEquals("Error: bad request", exception.getMessage());
    }


    @Test
    void testLoginSuccess() throws DataAccessException {
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        RegisterRequest rr = new RegisterRequest(uData.username(),uData.password(),uData.email());
        uService.register(rr);
        LoginRequest request = new LoginRequest("testUser", "password");

        // when
        RegLogResult result = uService.login(request);

        // then
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull( result.authToken());
    }
    @Test
    //bad pswd
    void testLoginFailure() throws DataAccessException {
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        LoginRequest request = new LoginRequest("testUser", "pswd");

        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            uService.login(request);
        });

        assertEquals(401, exception.statusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    void testLogoutSuccess() throws DataAccessException {
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        LogoutRequest request = new LogoutRequest(token);

        // when
        uService.logout(request);

        // then
        assertNull(authDAO.findAuth("testUser"));
        assertNull(authDAO.getAuth(token));
    }
    @Test
    void testLogoutFailure() throws DataAccessException {
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        LogoutRequest request = new LogoutRequest("12345");

        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            uService.logout(request);
        });

        assertEquals(401, exception.statusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }
}