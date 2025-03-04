package service;

import Request.*;
import Result.*;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegLogResult register(RegisterRequest registerRequest) throws DataAccessException {
        try {
            if (registerRequest.username() != null && registerRequest.password() != null && registerRequest.email() != null) {
                if (userDAO.getUser(registerRequest.username()) == null) {
                    UserData uData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
                    userDAO.createUser(uData);
                    AuthData aDataWrapper = new AuthData(null, uData.username());
                    authDAO.createAuth(aDataWrapper);
                    String token = authDAO.findAuth(uData.username());
                    return new RegLogResult(uData.username(), token);
                } else {
                    throw new DataAccessException(403, "Error: already taken");
                }
            }else{
                throw new DataAccessException(400, "Error: bad request");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.StatusCode(), e.getMessage());
        }
    }

    public RegLogResult login(LoginRequest loginRequest) throws DataAccessException {
        try {
            String user = loginRequest.username();
            if(userDAO.getUser(user) != null && userDAO.getUser(user).password().equals(loginRequest.password())){
                if (authDAO.findAuth(loginRequest.username()) != null){
                    String oldToken = authDAO.findAuth(loginRequest.username());
                    authDAO.deleteAuth(oldToken);
                }
                AuthData aDataWrapper = new AuthData(null, loginRequest.username());
                authDAO.createAuth(aDataWrapper);
                String token = authDAO.findAuth(loginRequest.username());
                return new RegLogResult(loginRequest.username(), token);
            } else{
                throw new DataAccessException(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.StatusCode(), e.getMessage());
        }
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        try {
            if(authDAO.getAuth(logoutRequest.authToken()) != null){
                authDAO.deleteAuth(logoutRequest.authToken());
            } else{
                throw new DataAccessException(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.StatusCode(), e.getMessage());
        }
    }
}
