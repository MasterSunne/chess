package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer,GameData> gameMap = new HashMap<>();

    @Override
    public void createGame(GameData g) throws DataAccessException{
        //only done after authenticating the user and checking that no game exists with the same name
        //service handles game ID creation
        gameMap.put(g.gameID(),g);
    }

    @Override
    public GameData findGame(String gameName) throws DataAccessException {
        try{
            for (GameData gData : gameMap.values()) {
                if (gData.gameName().equals(gameName)){
                    return gData;
                }
            }
            return null;
        } catch (Exception e) {
            throw new DataAccessException(400,e.getMessage());
        }
    }

    @Override
    public GameData getGame(Integer id) throws DataAccessException {
        return gameMap.get(id);
    }

    @Override
    public ArrayList<GameData> listGames(){
        ArrayList<GameData> games = new ArrayList<>();
        for (Map.Entry<Integer, GameData> entry : gameMap.entrySet()) {
            games.add(entry.getValue());
        }
        return games;
    }

    @Override
    public void updateGame(String newUser, String newColor, int id){
        GameData changingGame = gameMap.get(id);
        gameMap.remove(id);
        GameData newGame = null;

        if (newColor != null && newColor.equals("WHITE")) {
            newGame = new GameData(id, newUser, changingGame.blackUsername(), changingGame.gameName(), changingGame.game());
        } else if (newColor.equals("BLACK")){
            newGame = new GameData(id,changingGame.whiteUsername(), newUser, changingGame.gameName(), changingGame.game());
        }

        gameMap.put(id,newGame);
    }

    @Override
    public void clear() throws DataAccessException {
        gameMap.clear();
    }
}
