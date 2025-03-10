package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{

    final private HashMap<String, AuthData> authMap = new HashMap<>();


    public static String generateToken() {
        return UUID.randomUUID().toString();}

    @Override
    public AuthData createAuth(AuthData a) throws DataAccessException {
        return null;
    }

    @Override
    public String findAuth(String user) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
