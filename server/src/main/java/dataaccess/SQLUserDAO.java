package dataaccess;

import model.UserData;

import java.util.HashMap;

public class SQLUserDAO implements UserDAO{


    @Override
    public void createUser(UserData u) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE UserData";
        executeUpdate(statement);
    }
}
