package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> authMap = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();}

    @Override
    public AuthData createAuth(AuthData a) throws DataAccessException {
        try {
            if (a.username() != null){
                String token = generateToken();
                AuthData authenticated = new AuthData(token,a.username());
                authMap.put(token,authenticated);
                return authenticated;
            }
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
        return null;
    }

    //finds authToken given username
    @Override
    public String findAuth(String username) throws DataAccessException{
        try {
            for (AuthData aData : authMap.values()) {
                if (aData.username().equals(username)) {
                    return aData.authToken();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(401,"Error: unauthorized");
        }
        return null;
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try {
            return authMap.get(token);
        } catch (Exception e) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        try {
            authMap.remove(token);
        } catch (Exception e) {
            throw new DataAccessException(401,"Error: unauthorized");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        authMap.clear();
    }
}
