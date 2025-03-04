package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String,UserData> userMap = new HashMap<>();

    @Override
    public void createUser(UserData u) {
        // happens after the service checking that the username isn't taken already
        userMap.put(u.username(),u);
    }

    @Override
    public UserData getUser(String username){
        return userMap.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        userMap.clear();
    }
}
