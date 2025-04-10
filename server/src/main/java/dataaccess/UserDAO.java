package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData u) throws DataAccessException;

    public UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;

    boolean verifyPassword(String password, String dbPass);
}
