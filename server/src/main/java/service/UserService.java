package service;

import Request.*;
import Result.*;
import dataaccess.*;
import model.AuthData;
import model.UserData;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;

    }

    public RegLogResult register(RegisterRequest registerRequest) throws ServiceException {
        RegLogResult result = null;
        try {
            if (userDAO.getUser(registerRequest.username()) == null) {
                UserData uData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
                userDAO.createUser(uData);
                AuthData aDataWrapper = new AuthData(null, uData.username());
                authDAO.createAuth(aDataWrapper);
                String token = authDAO.findAuth(uData.username());
                result = new RegLogResult(uData.username(), token);
            }
        } catch (DataAccessException e) {
            throw new ServiceException(401, e.getMessage());
        }
        return result;
    }

    public RegLogResult login(LoginRequest loginRequest) throws DataAccessException {
        RegLogResult result = null;
        try {
            if(userDAO.getUser(loginRequest.username()) != null){
                AuthData aDataWrapper = new AuthData(null, loginRequest.username());
                authDAO.createAuth(aDataWrapper);
                String token = authDAO.findAuth(loginRequest.username());
                result = new RegLogResult(loginRequest.username(), token);
        }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData aData = authDAO.getAuth(logoutRequest.authToken());
        if(aData != null){
            authDAO.deleteAuth(logoutRequest.authToken());
        }
    }
}
