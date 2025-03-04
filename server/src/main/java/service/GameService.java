package service;

import Request.CreateGameRequest;
import Request.JoinGameRequest;
import Request.ListGamesRequest;
import Result.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException, ServiceException {
        try {
            if(authDAO.getAuth(listGamesRequest.authToken()) != null){
                ArrayList<GameData> games = gameDAO.listGames();
                return new ListGamesResult(games);
             }
        } catch (DataAccessException e) {
            throw new ServiceException(400,e.getMessage());
        }
        return null;
    }

    static int x = 1;

    public CreateGameResult createGame(String authToken,CreateGameRequest createGameRequest) throws DataAccessException, ServiceException {
        if(authDAO.getAuth(authToken) != null){
            if(gameDAO.findGame(createGameRequest.gameName()) == null){
                ChessGame newGame = new ChessGame();
                GameData gData = new GameData(x++, null, null,createGameRequest.gameName(),newGame);
                gameDAO.createGame(gData);
                return new CreateGameResult(gData.gameID());
            }
        } else{
            throw new ServiceException(401, "Error: unauthorized");
            }
        throw new ServiceException(401, "Error: unauthorized");
    }

    public void joinGame(String authToken,JoinGameRequest joinGameRequest) throws DataAccessException {
      if(authDAO.getAuth(authToken) != null){
            AuthData aData = new AuthData(authToken,authDAO.getAuth(authToken).username());
//            GameData searchingGame = gameDAO.getGame(joinGameRequest.gameID());
            gameDAO.updateGame(aData.username(),joinGameRequest.playerColor(),joinGameRequest.gameID());
        }else{
          throw new DataAccessException(401, "Error: unauthorized");
      }
    }
}
