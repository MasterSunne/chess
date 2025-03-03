package service;

import Request.CreateGameRequest;
import Request.JoinGameRequest;
import Request.ListGamesRequest;
import Result.*;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }
//
//    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
//      if(getAuth(authToken) != null){
////            create new list to add games to
//            listGames();
//            return new ListGamesResult();
//       }
//    }
//
//    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
//        if(getAuth(authToken) != null){
//            if(getGame(gameName) = null){
//                createGame(gameName);
//                return new CreateGameResult();
//    }
//    }
//    }
//
//    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
//      if(getAuth(authToken) != null){
//            GameData searchingGame = findGame(gameID);
//            updateGame(username,playerColor,searchingGame);
//            return new LogoutResult();
//}
//    }
}
