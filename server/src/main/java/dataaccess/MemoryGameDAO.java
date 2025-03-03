package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.List;


public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer,GameData> gameMap = new HashMap<>();

    public void createGame(GameData g){
        //only done after authenticating the user and checking that no game exists with the same name
        //service handles game ID creation
        gameMap.put(g.gameID(),g);
    }

    public GameData getGame(int id){ return gameMap.get(id); }

    public List<GameData> listGames(){throw new UnsupportedOperationException("Method not implemented yet");}

    public void updateGame(String newUser, String newColor, int id){
        GameData changingGame = gameMap.get(id);
        gameMap.remove(id);
        GameData newGame = null;

        if (newColor.equals("WHITE")) {
            newGame = new GameData(id, newUser, changingGame.blackUsername(), changingGame.gameName(), changingGame.game());
        } else if (newColor.equals("BLACK")){
            newGame = new GameData(id,changingGame.whiteUsername(), newUser, changingGame.gameName(), changingGame.game());
        }

        gameMap.put(id,newGame);
    }
}
