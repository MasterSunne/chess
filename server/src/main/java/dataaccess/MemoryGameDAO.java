package dataaccess;

import model.GameData;

import java.util.List;
import java.util.Optional;

public class MemoryGameDAO implements GameDAO{
    @Override
    public void createGame(GameData g){throw new UnsupportedOperationException("Method not implemented yet");}

    @Override
    public Optional<GameData> getGame(int id){throw new UnsupportedOperationException("Method not implemented yet"); }

    @Override
    public List<GameData> listGames(){throw new UnsupportedOperationException("Method not implemented yet");}

    @Override
    public void updateGame(int id){throw new UnsupportedOperationException("Method not implemented yet");}
}
