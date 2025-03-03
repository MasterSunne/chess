package dataaccess;

import model.GameData;
import java.util.*;

public interface GameDAO {
    void createGame(GameData g) throws DataAccessException;

    public GameData getGame(int id) throws DataAccessException;

    public List<GameData> listGames() throws DataAccessException;

    public void updateGame(String newUser, String newColor, int id) throws DataAccessException;
}
