package service;

import chess.ChessGame;
import model.GameData;
import request.*;
import result.*;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    // Declare variables as instance (global) variables
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gService;

    @BeforeEach
    void initialization() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gService = new GameService(authDAO, gameDAO);
        GameService.x = 1;
    }

    @Test
    void testCreateGameSuccess() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");

        CreateGameRequest request = new CreateGameRequest("testGame");

        //when
        CreateGameResult expectedResult = new CreateGameResult(1);
        CreateGameResult result = gService.createGame(token, request);

        //then
        assertEquals(expectedResult,result);
        assertEquals(1, result.gameID());
    }
    @Test
    void testCreateGameFailure() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = "12345";

        CreateGameRequest request = new CreateGameRequest("testGame");

        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            gService.createGame(token, request);
        });

        assertEquals(401, exception.statusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    void testListGamesSuccess() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        CreateGameRequest gameRequest = new CreateGameRequest("testGame");
        gService.createGame(token,gameRequest);

        ListGamesRequest request = new ListGamesRequest(token);

        // when
        ArrayList<GameData> testArray = new ArrayList<GameData>();
        GameData testData = new GameData(1,null,null,"testGame",new ChessGame());
        testArray.add(testData);
        ListGamesResult expectedResult = new ListGamesResult(testArray);
        ListGamesResult result = gService.listGames(request);

        // then
        assertEquals(expectedResult,result);
    }
    @Test
    void testListGamesFailure() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        CreateGameRequest gameRequest = new CreateGameRequest("testGame");
        gService.createGame(token,gameRequest);

        ListGamesRequest request = new ListGamesRequest("12345");


        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            gService.listGames(request);
        });

        assertEquals(401, exception.statusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    void testJoinGameSuccess() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        CreateGameRequest gameRequest = new CreateGameRequest("testGame");
        gService.createGame(token,gameRequest);

        JoinGameRequest request = new JoinGameRequest(token,"WHITE",1);

        // when
        gService.joinGame(token,request);

        ArrayList<GameData> testArray = new ArrayList<GameData>();
        GameData testData = new GameData(1,"testUser",null,"testGame",new ChessGame());
        testArray.add(testData);
        ListGamesResult expectedResult = new ListGamesResult(testArray);
        ListGamesRequest listRequest = new ListGamesRequest(token);
        ListGamesResult result = gService.listGames(listRequest);

        // then
        assertEquals(expectedResult,result);
    }
    @Test
    void testJoinGameFailure() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        CreateGameRequest gameRequest = new CreateGameRequest("testGame");
        gService.createGame(token,gameRequest);

        JoinGameRequest request = new JoinGameRequest(token,"WHITE",1);

        // when/then
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            gService.joinGame("12345",request);
        });

        assertEquals(401, exception.statusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    void testClearSuccess() throws DataAccessException{
        // if
        UserData uData = new UserData("testUser", "password", "testUser@test.com");
        userDAO.createUser(uData);
        AuthData aData = new AuthData(null,"testUser");
        authDAO.createAuth(aData);
        String token = authDAO.findAuth("testUser");
        CreateGameRequest gameRequest = new CreateGameRequest("testGame");
        gService.createGame(token,gameRequest);
        JoinGameRequest request = new JoinGameRequest(token,"WHITE",1);
        gService.joinGame(token,request);

        // when
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();

        // then
        assertNull(authDAO.getAuth(token));
        assertNull(authDAO.findAuth("testUser"));
        assertNull(userDAO.getUser("testUser"));
        assertNull(gameDAO.getGame(1));
        assertNull(gameDAO.findGame("testGame"));
    }
}