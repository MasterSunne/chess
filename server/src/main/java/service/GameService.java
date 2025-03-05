package service;

import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        try {
            if(authDAO.getAuth(listGamesRequest.authToken()) != null){
                ArrayList<GameData> games = gameDAO.listGames();
                return new ListGamesResult(games);
             } else{
                throw new DataAccessException(401,"Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
    }


    static int x = 1;
    public CreateGameResult createGame(String authToken,CreateGameRequest createGameRequest) throws DataAccessException {
        try {
            if(authDAO.getAuth(authToken) != null){
                if(gameDAO.findGame(createGameRequest.gameName()) == null){
                    ChessGame newGame = new ChessGame();
                    GameData gData = new GameData(x++, null, null,createGameRequest.gameName(),newGame);
                    gameDAO.createGame(gData);
                    return new CreateGameResult(gData.gameID());
                }
            } else{
                throw new DataAccessException(401, "Error: unauthorized");
                }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(),e.getMessage());
        }
        return null;
    }

    public void joinGame(String authToken,JoinGameRequest joinGameRequest) throws DataAccessException {
        try {
            if(authDAO.getAuth(authToken) != null){
                if (joinGameRequest.gameID() != null) {
                    AuthData aData = new AuthData(authToken,authDAO.getAuth(authToken).username());
      //            GameData searchingGame = gameDAO.getGame(joinGameRequest.gameID());
                    gameDAO.updateGame(aData.username(),joinGameRequest.playerColor(),joinGameRequest.gameID());
                } else{
                    throw new DataAccessException(400,"Error: bad request");
                }
            }else{
                throw new DataAccessException(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
    }
}
