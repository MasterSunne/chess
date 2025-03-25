package client;

import org.junit.jupiter.api.*;
import request.RegisterRequest;
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
        facade = new ServerFacade("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void testRegisterUserSuccess() throws Exception {
        RegisterRequest rr = new RegisterRequest("player1", "password", "p1@email.com");
        var result = facade.registerUser(rr);

        assertNotNull(result);
        assertEquals("player1", result.username());
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void testRegisterUser_Failure() throws ResponseException {
        // Arrange
        String serverUrl = "http://test-server.com";
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        String username = "existingUser";
        String password = "testPass123";
        String email = "existing@example.com";
        RegisterRequest rr = new RegisterRequest(username, password, email);
        facade.registerUser(rr);

        // Act & Assert
        assertThrows(ResponseException.class, () -> {
            serverFacade.registerUser(rr);
        });
    }

}
