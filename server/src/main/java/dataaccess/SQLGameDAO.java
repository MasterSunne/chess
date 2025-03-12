package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO extends SqlDaoBase implements GameDAO {

    @Override
    public void createGame(GameData g) throws DataAccessException {
        try {
            var statement = "INSERT INTO gamedata (id, whiteUsername, blackUsername, gameName, gameJSON) VALUES (?,?,?,?,?)";
            int id = g.gameID();
            String whiteUser = g.whiteUsername();
            String blackUser = g.blackUsername();
            if (g.gameName() != null && g.game() != null) {
                String name = g.gameName();
                String json = new Gson().toJson(g.game());
                executeUpdate(statement, id, whiteUser, blackUser, name, json);
            } else{
                throw new DataAccessException(500,"can't be null");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(500,e.getMessage());
        }
    }

    @Override
    public GameData findGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, gameJSON FROM gamedata WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public GameData getGame(Integer id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, gameJSON FROM gamedata WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, gameJSON FROM gamedata";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void updateGame(String newUser, String newColor, int id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, gameJSON FROM gamedata WHERE id=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    updateGameHelper(newUser, newColor, id, rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private void updateGameHelper(String newUser, String newColor, int id, ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            GameData changingGame = readGame(rs);
            if ((newColor.equals("WHITE") && changingGame.whiteUsername() != null)
                    || (newColor.equals("BLACK") && changingGame.blackUsername() != null)) {
                throw new DataAccessException(403, "Error: already taken");
            }
            if (newColor.equals("WHITE")) {
                var statement2 = "UPDATE gamedata SET whiteUsername = '" + newUser + "' WHERE id=?";
                executeUpdate(statement2, id);

            } else if (newColor.equals("BLACK")) {
                var statement3 = "UPDATE gamedata SET blackUsername = '" + newUser + "' WHERE id=?";
                executeUpdate(statement3, id);
            }
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("id");
        var whiteUser = rs.getString("whiteUsername");
        var blackUser = rs.getString("blackUsername");
        var name = rs.getString("gameName");
        var json = rs.getString("gameJSON");
        ChessGame gameObj = new Gson().fromJson(json, ChessGame.class);

        return new GameData(gameID,whiteUser,blackUser,name,gameObj);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE gamedata";
        executeUpdate(statement);
    }
}
