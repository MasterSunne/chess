package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import server.Server; //from the server module not the client module
import static org.junit.jupiter.api.Assertions.*;
import server.ServerFacade;
import server.ResponseException;

import java.util.ArrayList;


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

    @BeforeEach
    // Clear the database before all tests
    public void clear() {
        try {
            facade.clearAll();
        } catch (ResponseException e) {
            System.err.println("Failed to clear database: " + e.getMessage());
        }
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
    public void testRegisterUserFailure() throws ResponseException {
        // Arrange
        String username = "existingUser";
        String password = "12345";
        String email = "existing@mail.com";
        RegisterRequest rr = new RegisterRequest(username, password, email);
        facade.registerUser(rr);

        // Act & Assert
        assertThrows(ResponseException.class, () -> {
            facade.registerUser(rr);
        });
    }

    @Test
    public void testLoginSuccess() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        facade.registerUser(rr);
        var request = new LoginRequest("bob","bobPass");
        var result = facade.loginUser(request);

        assertNotNull(result);
        assertEquals("bob", result.username());
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void testLoginFailure() throws ResponseException{
        var loginRequest = new LoginRequest("unregisteredUser", "badPassword");
        assertThrows(ResponseException.class, () ->
                facade.loginUser(loginRequest));
    }

    @Test
    public void testLogoutSuccess() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();
        var logoutRequest = new LogoutRequest(token);
        facade.logoutUser(logoutRequest);
        ListGamesRequest lgr = new ListGamesRequest(token);
        assertThrows(ResponseException.class, () ->
                facade.listGames(lgr));
    }

    @Test
    public void testLogoutFailure() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        facade.registerUser(rr);

        var logoutRequest = new LogoutRequest("randomToken");
        assertThrows(ResponseException.class, () ->
                facade.logoutUser(logoutRequest));
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();

        var createGameRequest = new CreateGameRequest("newGame");
        var createResult = facade.createGame(token, createGameRequest);

        assertEquals(new CreateGameResult(1),createResult);
    }

    @Test
    public void testCreateGameFailure() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);

        var createGameRequest = new CreateGameRequest("newGame");

        assertThrows(ResponseException.class, () ->
                facade.createGame("randomToken", createGameRequest));
    }

    @Test
    public void testListGamesSuccess() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();

        var createGameRequest = new CreateGameRequest("newGame");
        facade.createGame(token, createGameRequest);

        var listResult = facade.listGames(new ListGamesRequest(token));
        assertNotNull(listResult);
        assertFalse(listResult.games().isEmpty());
    }

    @Test
    public void testListGamesFailure() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();

        var createGameRequest = new CreateGameRequest("newGame");
        facade.createGame(token, createGameRequest);

        assertThrows(ResponseException.class, () ->
                facade.listGames(new ListGamesRequest("randomToken")));
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();

        var createGameRequest = new CreateGameRequest("newGame");
        facade.createGame(token, createGameRequest);

        facade.joinGame(new JoinGameRequest(token,"WHITE",1));
        var listResult = facade.listGames(new ListGamesRequest(token));
        var list = listResult.games();
        assertEquals("bob", list.getFirst().whiteUsername());

    }

    @Test
    public void testJoinGameFailure() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();

        var createGameRequest = new CreateGameRequest("newGame");
        facade.createGame(token, createGameRequest);

        assertThrows(ResponseException.class, () ->
                facade.joinGame(new JoinGameRequest(token,"white",1)));
    }

    @Test
    public void testClearAll() throws ResponseException{
        RegisterRequest rr = new RegisterRequest("bob","bobPass", "bob@mail.com");
        var result = facade.registerUser(rr);
        String token = result.authToken();

        var createGameRequest = new CreateGameRequest("newGame");
        facade.createGame(token, createGameRequest);

        facade.clearAll();

        assertThrows(ResponseException.class, () ->
                facade.joinGame(new JoinGameRequest(token,"white",1)));
    }
}
