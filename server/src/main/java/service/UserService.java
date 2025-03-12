package service;

import request.*;
import result.*;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

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
                    AuthData newAuth = authDAO.createAuth(aDataWrapper);
                    String token = newAuth.authToken();
                    return new RegLogResult(uData.username(), token);
                } else {
                    throw new DataAccessException(403, "Error: already taken");
                }
            }else{
                throw new DataAccessException(400, "Error: bad request");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
    }

    public RegLogResult login(LoginRequest loginRequest) throws DataAccessException {
        try {
            String user = loginRequest.username();
            if(userDAO.getUser(user) != null
                    && BCrypt.checkpw(loginRequest.password(),userDAO.getUser(user).password())){

                AuthData aDataWrapper = new AuthData(null, loginRequest.username());
                AuthData aData = authDAO.createAuth(aDataWrapper);
                String token = aData.authToken();
                return new RegLogResult(loginRequest.username(), token);
            } else{
                throw new DataAccessException(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
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
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
    }
}
