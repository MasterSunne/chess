package dataaccess;

import model.UserData;

import java.util.Optional;

public class MemoryUserDAO implements UserDAO{
    @Override
    public void createUser(UserData u) {throw new UnsupportedOperationException("Method not implemented yet");}

    @Override
    public Optional<UserData> getUser(String username){throw new UnsupportedOperationException("Method not implemented yet");}
}
