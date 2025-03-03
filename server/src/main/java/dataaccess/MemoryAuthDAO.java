package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> authMap = new HashMap<>();
    public static String generateToken() {
        return UUID.randomUUID().toString();}

    public void createAuth(AuthData a){
        if (a.username() != null){
            String token = generateToken();
            AuthData authenticated = new AuthData(token,a.username());

            authMap.put(token,authenticated);
        }
    }

    public AuthData getAuth(String token){ return authMap.get(token); }

    public void deleteAuth(String token){ authMap.remove(token); }
}
