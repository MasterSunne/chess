package service;

import Request.LoginRequest;
import Request.LogoutRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.LogoutResult;
import Result.RegisterResult;
import dataaccess.*;
import model.AuthData;

public class UserService {
    private final String dataAccess;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    public UserService(String dataAccess) {
        this.dataAccess = dataAccess;
        initializeDAOs();
    }

    private void initializeDAOs() {
        if ("MEMORY".equals(dataAccess)) {
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
            userDAO = new MemoryUserDAO();
        } else {
            // sql dataAccess types here later
            throw new IllegalArgumentException("Unsupported data access type");
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        try {
            if(userDAO.getUser(registerRequest.username()) == null){
                  userDAO.createUser(userData);
                  authDAO.createAuth(authData);
             }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return RegisterResult;
    }

    public LoginResult login(LoginRequest loginRequest) {
        if(userDAO.getUser(loginRequest.username()) != null){
            authDAO.createAuth(authData);
            return new LoginResult(username, authToken);
    }
    }

    public void logout(LogoutRequest logoutRequest) {
        AuthData aData = getAuth(authToken);
      if(aData != null){
            deleteAuth(aData);
            return new LogoutResult();
}
    }
}
