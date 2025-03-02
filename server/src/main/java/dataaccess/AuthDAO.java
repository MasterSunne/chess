package dataaccess;

import model.AuthData;
import model.GameData;
import java.util.Optional;

public interface AuthDAO {
    void createGame(AuthData a);

    public Optional<GameData> getAuth(String token);

    void deleteAuth(String token);
}
