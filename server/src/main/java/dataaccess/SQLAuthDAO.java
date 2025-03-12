package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();}

    @Override
    public AuthData createAuth(AuthData a) throws DataAccessException {
        try {
            var statement = "INSERT INTO AuthData (username, authToken) VALUES (?, ?)";
            String token = generateToken();
            String username = a.username();
            var id = executeUpdate(statement, username, token);
            return new AuthData(token,username);
        } catch (DataAccessException e) {
            throw new DataAccessException(500,e.getMessage());
        }
    }

    @Override
    public String findAuth(String user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM AuthData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs).authToken();
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }


    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM AuthData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        var statement = "DELETE FROM AuthData WHERE authToken=?";
        executeUpdate(statement, token);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var token = rs.getString("authToken");
        var user = rs.getString("username");
        return new AuthData(token, user);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE AuthData";
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

    private final String[] createStatements = {
            """
            -- -----------------------------------------------------
            -- Schema chess_server
            -- -----------------------------------------------------
            CREATE SCHEMA IF NOT EXISTS `chess_server` DEFAULT CHARACTER SET utf8 ;
            USE `chess_server` ;
            
            -- -----------------------------------------------------
            -- Table `chess_server`.`AuthData`
            -- -----------------------------------------------------
            DROP TABLE IF EXISTS `chess_server`.`AuthData` ;
            
            CREATE TABLE IF NOT EXISTS `chess_server`.`AuthData` (
              `username` VARCHAR(30) NOT NULL,
              `authToken` VARCHAR(60) NOT NULL,
              UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
              UNIQUE INDEX `token_UNIQUE` (`authToken` ASC) VISIBLE)
            ENGINE = InnoDB;
            
            
            -- -----------------------------------------------------
            -- Table `chess_server`.`GameData`
            -- -----------------------------------------------------
            DROP TABLE IF EXISTS `chess_server`.`GameData` ;
            
            CREATE TABLE IF NOT EXISTS `chess_server`.`GameData` (
              `idGameData` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(30) NULL,
              `blackUsername` VARCHAR(30) NULL,
              `gameName` VARCHAR(45) NULL,
              `gameJSON` VARCHAR(400) NOT NULL,
              PRIMARY KEY (`idGameData`),
              UNIQUE INDEX `idGameData_UNIQUE` (`idGameData` ASC) VISIBLE,
              UNIQUE INDEX `gameName_UNIQUE` (`gameName` ASC) VISIBLE)
            ENGINE = InnoDB;
            
            
            -- -----------------------------------------------------
            -- Table `chess_server`.`UserData`
            -- -----------------------------------------------------
            DROP TABLE IF EXISTS `chess_server`.`UserData` ;
            
            CREATE TABLE IF NOT EXISTS `chess_server`.`UserData` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `username` VARCHAR(30) NOT NULL,
              `email` VARCHAR(150) NOT NULL,
              `password` VARCHAR(40) NOT NULL,
              UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
              UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
              UNIQUE INDEX `password_UNIQUE` (`password` ASC) VISIBLE,
              PRIMARY KEY (`id`),
              UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
