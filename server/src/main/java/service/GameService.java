package service;

import Request.CreateGameRequest;
import Request.JoinGameRequest;
import Request.ListGamesRequest;
import Result.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException, ServiceException {
        try {
            if(authDAO.getAuth(listGamesRequest.authToken()) != null){
                ArrayList<GameData> games = gameDAO.listGames();
                ListGamesResult result = new ListGamesResult(games);
                return result;
             }
        } catch (DataAccessException e) {
            throw new ServiceException(400,e.getMessage());
        }
        return null;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        if(authDAO.getAuth(createGameRequest.) != null){
            if(getGame(gameName) = null){
                createGame(gameName);
                return new CreateGameResult();
    }
    }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
      if(getAuth(authToken) != null){
            GameData searchingGame = findGame(gameID);
            updateGame(username,playerColor,searchingGame);
            return new LogoutResult();
}
    }
}
