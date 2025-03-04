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
            if (registerRequest.username() != null && registerRequest.password() != null && registerRequest.email() != null) {
                if (userDAO.getUser(registerRequest.username()) == null) {
                    UserData uData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
                    userDAO.createUser(uData);
                    AuthData aDataWrapper = new AuthData(null, uData.username());
                    authDAO.createAuth(aDataWrapper);
                    String token = authDAO.findAuth(uData.username());
                    result = new RegLogResult(uData.username(), token);
                } else {
                    throw new ServiceException(403, "Error: already taken");
                }
            }else{
                throw new ServiceException(400, "Error: bad request");
            }
        } catch (DataAccessException e) {
            throw new ServiceException(500, e.getMessage());
        }
        return result;
    }

    public RegLogResult login(LoginRequest loginRequest) throws DataAccessException {
        RegLogResult result = null;
        try {
            String user = loginRequest.username();
            if(userDAO.getUser(user) != null && userDAO.getUser(user).password().equals(loginRequest.password())){
                AuthData aDataWrapper = new AuthData(null, loginRequest.username());
                authDAO.createAuth(aDataWrapper);
                String token = authDAO.findAuth(loginRequest.username());
                result = new RegLogResult(loginRequest.username(), token);
                return result;
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        throw new DataAccessException(401, "Error: unauthorized");
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException, ServiceException {
        if(authDAO.getAuth(logoutRequest.authToken()) != null){
            authDAO.deleteAuth(logoutRequest.authToken());
        } else{
            throw new DataAccessException(401, "Error: unauthorized");
        }
    }
}
