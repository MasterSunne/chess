package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private GameDAO gameDAO;

    @BeforeEach
    void initialization() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        // when
        gameDAO.createGame(testData);
        // then
        assertEquals(testData,gameDAO.findGame("newGame"));
        assertEquals(testData, gameDAO.getGame(1));
    }

    @Test
    public void testCreateGameFailure() throws DataAccessException {
        // if
        GameData testData = new GameData(1,null,null,null,null);
        // when
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            gameDAO.createGame(testData);
        });
        // then
        assertEquals(500, exception.statusCode());
        assertEquals("can't be null",exception.getMessage());
    }


    @Test
    public void testFindGameSuccess() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);
        // when
        GameData output = gameDAO.findGame("newGame");
        // then
        assertEquals(testData.whiteUsername(),output.whiteUsername());
        assertEquals(testData.blackUsername(),output.blackUsername());
        assertEquals(testData.gameID(),output.gameID());
        assertEquals(testData.game(),output.game());
    }

    @Test
    public void testFindGameFailure() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);
        // when
        GameData output = gameDAO.findGame("1234");
        // then
        assertNull(output);
    }


    @Test
    public void testGetGameSuccess() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);
        // when
        GameData output = gameDAO.getGame(1);
        // then
        assertEquals(testData.whiteUsername(),output.whiteUsername());
        assertEquals(testData.blackUsername(),output.blackUsername());
        assertEquals(testData.gameName(),output.gameName());
        assertEquals(testData.game(),output.game());

    }

    @Test
    public void testGetGameFailure() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);
        // when
        GameData output = gameDAO.getGame(2);
        // then
        assertNull(output);

    }


    @Test
    public void testListGamesSuccess() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);

        ArrayList<GameData> expectedList = new ArrayList<>();
        expectedList.add(testData);

        // when
        ArrayList<GameData> output = gameDAO.listGames();
        // then
        assertEquals(expectedList,output);
    }


    @Test
    public void testUpdateGameSuccess() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);

        GameData changedData = new GameData(1,"dude",null,"newGame",newGame);

        ArrayList<GameData> expectedList = new ArrayList<>();
        expectedList.add(changedData);

        // when
        gameDAO.updateGame("dude","WHITE",1);
        ArrayList<GameData> output = gameDAO.listGames();
        // then
        assertEquals(expectedList,output);
    }

    @Test
    public void testUpdateGameFailure() throws DataAccessException{
        // if
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,"bruh",null,"newGame",newGame);
        gameDAO.createGame(testData);

        // when
        DataAccessException exception = assertThrows(DataAccessException.class,() -> {
            gameDAO.updateGame("dude","WHITE",1);
        });
        // then
        assertEquals(403,exception.statusCode());
        assertEquals("Error: already taken",exception.getMessage());

    }


    @Test
    public void testClear() throws DataAccessException{
        ChessGame newGame = new ChessGame();
        GameData testData = new GameData(1,null,null,"newGame",newGame);
        gameDAO.createGame(testData);

        ArrayList<GameData> expectedList = new ArrayList<>();

        gameDAO.updateGame("dude","WHITE",1);
        GameData testData2 = new GameData(2,null,null,"nextGame",newGame);
        gameDAO.createGame(testData2);

        // when
        gameDAO.clear();

        // then
        assertNull(gameDAO.getGame(1));
        assertNull(gameDAO.findGame("newGame"));
        assertNull(gameDAO.getGame(2));
        assertNull(gameDAO.findGame("nextGame"));
        assertEquals(expectedList,gameDAO.listGames());
    }
}
