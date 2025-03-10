package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLGameDAO implements GameDAO{

    final private HashMap<Integer,GameData> gameMap = new HashMap<>();


    @Override
    public void createGame(GameData g) throws DataAccessException {

    }

    @Override
    public GameData findGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(Integer id) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(String newUser, String newColor, int id) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
