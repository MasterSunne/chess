package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Optional;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public void createGame(AuthData a){throw new UnsupportedOperationException("Method not implemented yet");}

    @Override
    public Optional<GameData> getAuth(String token){throw new UnsupportedOperationException("Method not implemented yet"); }

    @Override
    public void deleteAuth(String token){throw new UnsupportedOperationException("Method not implemented yet");}
}
