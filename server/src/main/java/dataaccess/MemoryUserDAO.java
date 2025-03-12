package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String,UserData> userMap = new HashMap<>();

    @Override
    public void createUser(UserData u) throws DataAccessException{
        // happens after the service checking that the username isn't taken already
        try {
            userMap.put(u.username(),u);
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    @Override
    public boolean verifyPassword(String cleanPassword, String hashedPassword){
        return cleanPassword.equals(hashedPassword);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try {
            return userMap.get(username);
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            userMap.clear();
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }
}
