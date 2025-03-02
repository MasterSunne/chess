package dataaccess;

import model.GameData;
import java.util.*;
import java.util.Optional;

public interface GameDAO {
    void createGame(GameData g);

    public Optional<GameData> getGame(int id);

    public List<GameData> listGames();

    public void updateGame(int id);
}
