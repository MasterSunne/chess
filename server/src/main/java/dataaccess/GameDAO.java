package dataaccess;

import model.GameData;
import java.util.*;

public interface GameDAO {
    void createGame(GameData g) throws DataAccessException;

    GameData findGame(String gameName) throws DataAccessException;

    public GameData getGame(Integer id) throws DataAccessException;

    public ArrayList<GameData> listGames() throws DataAccessException;

    public void updateGame(String newUser, String newColor, int id) throws DataAccessException;

    void clear() throws DataAccessException;
}
