package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private UserDAO userDAO;

    @BeforeEach
    void initialization() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        //takes in UserData, no output
        // if
        UserData testData = new UserData("newUser","newPassword","new@mail.com");
        // when
        userDAO.createUser(testData);
        // then
        assertTrue(userDAO.verifyPassword("newPassword",userDAO.getUser("newUser").password()));
    }

    @Test
    public void testCreateUserFailure() throws DataAccessException {
        //takes in UserData, no output
        // if
        UserData testData = new UserData("newUser","newPassword",null);
        // when
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            userDAO.createUser(testData);
        });
        // then
        assertEquals(500, exception.statusCode());
        assertEquals("unable to update database: INSERT INTO userdata (username, password, email) " +
                "VALUES (?, ?, ?), Column 'email' cannot be null", exception.getMessage());
    }


    @Test
    public void testGetUserSuccess() throws DataAccessException {
        //takes in username, outputs UserData
        // if
        UserData expectedData = new UserData("newUser","newPassword","new@mail.com");
        userDAO.createUser(expectedData);
        // when
        UserData testData = userDAO.getUser("newUser");
        // then
        assertEquals(expectedData.username(),testData.username());
        assertEquals(expectedData.email(),testData.email());
        assertEquals(60, testData.password().length());
    }

    @Test
    public void testGetUserFailure() throws DataAccessException {
        //takes in username, outputs UserData
        // if
        UserData expectedData = new UserData("someUser","somePassword","something@mail.com");
        userDAO.createUser(expectedData);
        // when
        UserData testData = userDAO.getUser("newUser");
        // then
        assertNull(testData);
    }


    @Test
    public void testClearSuccess() throws DataAccessException {
        // if
        UserData expectedData = new UserData("newUser","newPassword","new@mail.com");
        userDAO.createUser(expectedData);

        UserData expectedData2 = new UserData("nextUser","nextPassword","next@mail.com");
        userDAO.createUser(expectedData2);

        // when
        userDAO.clear();

        // then
        assertNull(userDAO.getUser("newUser"));
        assertNull(userDAO.getUser("nextUser"));
    }
}
