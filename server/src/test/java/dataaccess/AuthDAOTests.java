package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private AuthDAO authDAO;

    @BeforeEach
    void initialization() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void testCreateAuthSuccess() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,"newUser");
        // when
        AuthData createdData = authDAO.createAuth(passInData);
        // then
        assertNotNull(createdData.authToken());
        assertEquals("newUser",createdData.username());
    }

    @Test
    void testCreateAuthFailure() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,null);
        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            authDAO.createAuth(passInData);
        });

        assertEquals(500, exception.statusCode());
        assertEquals("unable to update database: INSERT INTO authdata (username, authToken) " +
                "VALUES (?, ?), Column 'username' cannot be null", exception.getMessage());
    }


    @Test
    void testFindAuthSuccess() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData createdData = authDAO.createAuth(passInData);
        String expectedToken = createdData.authToken();
        // when
        String token = authDAO.findAuth("newUser");
        // then
        assertEquals(expectedToken,token);
    }

    @Test
    void testFindAuthFailure() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData createdData = authDAO.createAuth(passInData);
        String expectedToken = createdData.authToken();
        // when
        String token = authDAO.findAuth("differentUser");
        // then
        assertNotEquals(expectedToken,token);
        assertNull(token);
    }


    @Test
    void testGetAuthSuccess() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData expectedData = authDAO.createAuth(passInData);
        String createdToken = expectedData.authToken();
        // when
        AuthData resultData = authDAO.getAuth(createdToken);
        // then
        assertEquals(expectedData,resultData);
    }

    @Test
    void testGetAuthFailure() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData expectedData = authDAO.createAuth(passInData);
        String createdToken = expectedData.authToken();
        // when
        AuthData resultData = authDAO.getAuth("createdToken");
        // then
        assertNotEquals(expectedData,resultData);
        assertNull(resultData);
    }


    @Test
    void testDeleteAuthSuccess() throws DataAccessException{
        //takes in token, doesn't return
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData expectedData = authDAO.createAuth(passInData);
        String createdToken = expectedData.authToken();
        // when
        authDAO.deleteAuth(createdToken);
        // then
        assertNull(authDAO.getAuth(createdToken));
    }

    @Test
    void testDeleteAuthFailure() throws DataAccessException{
        //takes in token, doesn't return
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData expectedData = authDAO.createAuth(passInData);
        String createdToken = expectedData.authToken();
        // when
        authDAO.deleteAuth("createdToken");
        // then
        assertNotNull(authDAO.getAuth(createdToken));
    }


    @Test
    void testClearSuccess() throws DataAccessException{
        // if
        AuthData passInData = new AuthData(null,"newUser");
        AuthData expectedData = authDAO.createAuth(passInData);
        String createdToken = expectedData.authToken();

        AuthData passInData2 = new AuthData(null,"nextUser");
        AuthData expectedData2 = authDAO.createAuth(passInData2);
        String createdToken2 = expectedData2.authToken();

        // when
        authDAO.clear();

        // then
        assertNull(authDAO.getAuth(createdToken));
        assertNull(authDAO.getAuth(createdToken2));
        assertNull(authDAO.findAuth("newUser"));
        assertNull(authDAO.findAuth("nextUser"));
    }
}
