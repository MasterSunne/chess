package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(AuthData a) throws DataAccessException;

    String findAuth(String user) throws DataAccessException;

    public AuthData getAuth(String token) throws DataAccessException;

    void deleteAuth(String token) throws DataAccessException;

    void clear() throws DataAccessException;
}
