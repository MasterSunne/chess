package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{

    final private HashMap<Integer,GameData> gameMap = new HashMap<>();


    @Override
    public void createGame(GameData g) throws DataAccessException {
        try {
            var statement = "INSERT INTO GameData (id, whiteUsername, blackUsername, gameName, gameJSON) VALUES (?,?,?,?,?)";
            int id = g.gameID();
            String whiteUser = g.whiteUsername();
            String blackUser = g.blackUsername();
            String name = g.gameName();
            String json = new Gson().toJson(g.game());
            executeUpdate(statement, id, whiteUser, blackUser, name, json);
        } catch (DataAccessException e) {
            throw new DataAccessException(500,e.getMessage());
        }
    }

    @Override
    public GameData findGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameName FROM GameData WHERE gameName=?";
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
            var statement = "SELECT id FROM GameData WHERE id=?";
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
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, gameJSON FROM GameData";
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
        var statement = "TRUNCATE GameData";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
