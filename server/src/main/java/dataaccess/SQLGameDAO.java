package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
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
    } // could add functionality to reject creating games with existing names

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

    @Override
    public void leaveGame(String leavingColor, Integer id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // retrieve existing gameData
            var selectStatement = "SELECT id, whiteUsername, blackUsername, gameName, gameJSON FROM gamedata WHERE id=?";
            try (var psSelect = conn.prepareStatement(selectStatement)) {
                psSelect.setInt(1, id);
                try (var rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        dataReplaceTool(leavingColor, id, rs, conn);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private static void dataReplaceTool(String leavingColor, Integer id, ResultSet rs, Connection conn) throws SQLException, DataAccessException {
        // extract existing data
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJSON = rs.getString("gameJSON");

        // set correct username to null for replacement data
        if ("white".equalsIgnoreCase(leavingColor)) {
            whiteUsername = null;
        } else if ("black".equalsIgnoreCase(leavingColor)) {
            blackUsername = null;
        } else {
            throw new IllegalArgumentException("Invalid color specified. Must be 'white' or 'black'.");
        }

        // replace row in database
        var insertStatement = "REPLACE INTO gamedata (id, whiteUsername, blackUsername, gameName, gameJSON) VALUES (?, ?, ?, ?, ?)";
        try (var psInsert = conn.prepareStatement(insertStatement)) {
            psInsert.setInt(1, id);
            psInsert.setString(2, whiteUsername);
            psInsert.setString(3, blackUsername);
            psInsert.setString(4, gameName);
            psInsert.setString(5, gameJSON);
            psInsert.executeUpdate();
        } catch(Exception e){
            throw new DataAccessException(404, "Game not found with ID: " + id);
        }
    }

    public void updateGameJSON(int id, String newGameJSON) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // SQL query to update only the gameJSON column
            var updateStatement = "UPDATE gamedata SET gameJSON = ? WHERE id = ?";
            try (var ps = conn.prepareStatement(updateStatement)) {
                ps.setString(1, newGameJSON); // Set the new JSON value
                ps.setInt(2, id);            // Specify the game ID
                int rowsUpdated = ps.executeUpdate();

                // Check if any rows were updated
                if (rowsUpdated == 0) {
                    throw new DataAccessException(404, "No game found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("Unable to update game JSON: %s", e.getMessage()));
        }
    }

}
