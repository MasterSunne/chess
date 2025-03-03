package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;

    public AuthData getAuth(String token) throws DataAccessException;

    void deleteAuth(String token) throws DataAccessException;

    void clear() throws DataAccessException;
}
