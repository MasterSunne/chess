package client;

import org.junit.jupiter.api.*;
import server.Server; //from the server module not the client module
import static org.junit.jupiter.api.Assertions.*;
import server.ServerFacade;
import server.ResponseException;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeAll
    //clear the database

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void testRegisterUser_Success() throws ResponseException {
        // Arrange
        String serverUrl = "http://test-server.com";
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        String username = "testUser";
        String password = "testPass123";
        String email = "test@example.com";

        // Act
        Object result = serverFacade.registerUser(username, password, email);

        // Assert
        assertNotNull(result);
        assertEquals(username, result);
    }
    @Test
    public void testRegisterUser_Failure() {
        // Arrange
        String serverUrl = "http://test-server.com";
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        String username = "existingUser";
        String password = "testPass123";
        String email = "existing@example.com";
        serverFacade.registerUser(username, password, email);

        // Act & Assert
        assertThrows(ResponseException.class, () -> {
            serverFacade.registerUser(username, password, email);
        });
    }

}
