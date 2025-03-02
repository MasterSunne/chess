package dataaccess;

import model.UserData;
import java.util.Optional;

public interface UserDAO {
    void createUser(UserData u);

    public Optional<UserData> getUser(String username);
}
