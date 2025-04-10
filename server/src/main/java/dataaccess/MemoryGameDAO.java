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
        try {
            gameMap.put(g.gameID(),g);
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
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
        try {
            return gameMap.get(id);
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        try {
            ArrayList<GameData> games = new ArrayList<>();
            for (Map.Entry<Integer, GameData> entry : gameMap.entrySet()) {
                games.add(entry.getValue());
            }
            return games;
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    @Override
    public void updateGame(String newUser, String newColor, int id) throws DataAccessException {
        try {
            GameData changingGame = gameMap.get(id);
            gameMap.remove(id);
            GameData newGame = null;

            if ((newColor.equals("WHITE") && changingGame.whiteUsername() != null)
                    ||(newColor.equals("BLACK") && changingGame.blackUsername() != null)) {
                throw new DataAccessException(403,"Error: already taken");
            }
            if (newColor.equals("WHITE")) {
                newGame = new GameData(id, newUser, changingGame.blackUsername(), changingGame.gameName(), changingGame.game());
            } else if (newColor.equals("BLACK")){
                newGame = new GameData(id,changingGame.whiteUsername(), newUser, changingGame.gameName(), changingGame.game());
            }
            gameMap.put(id,newGame);
        } catch(DataAccessException e){
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
        catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            gameMap.clear();
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    @Override
    public void leaveGame(String leavingColor, Integer id) throws DataAccessException {

    }

    @Override
    public void updateGameJSON(int id, String newGameJSON) throws DataAccessException{

    }

}
