package service;

import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.Random;

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



    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        try {
            if (authDAO.getAuth(authToken) == null) {
                throw new DataAccessException(401, "Error: unauthorized");
            }
            if (gameDAO.findGame(createGameRequest.gameName()) != null) {
                throw new DataAccessException(400, "Error: game name already exists");
            }
            // generate unique 4-digit ID
            Random rand = new Random();
            int gameID;
            int maxAttempts = 100;
            do {
                gameID = 100 + rand.nextInt(900);
                if (maxAttempts-- <= 0){
                    throw new DataAccessException(500, "Error: couldn't generate game ID");
                }
            } while(gameDAO.getGame(gameID) != null);

            // create new game and put in database
            ChessGame newGame = new ChessGame();
            GameData gData = new GameData(gameID, null, null, createGameRequest.gameName(), newGame);
            gameDAO.createGame(gData);
            return new CreateGameResult(gData.gameID());
        } catch (DataAccessException e){
            throw new DataAccessException(e.statusCode(),e.getMessage());
        }
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
