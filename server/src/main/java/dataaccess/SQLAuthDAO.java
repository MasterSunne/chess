package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SQLAuthDAO extends SQL_DAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();}

    @Override
    public AuthData createAuth(AuthData a) throws DataAccessException {
        try {
            var statement = "INSERT INTO authdata (username, authToken) VALUES (?, ?)";
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
            var statement = "SELECT authToken, username FROM authdata WHERE username=?";
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
            var statement = "SELECT authToken, username FROM authdata WHERE authToken=?";
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
        var statement = "DELETE FROM authdata WHERE authToken=?";
        executeUpdate(statement, token);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var token = rs.getString("authToken");
        var user = rs.getString("username");
        return new AuthData(token, user);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE authdata";
        executeUpdate(statement);
    }


    private final String[] createStatements = {
            """
            
            -- -----------------------------------------------------
            -- Table `AuthData`
            -- -----------------------------------------------------
            DROP TABLE IF EXISTS `AuthData` ;
            """,

            """
            CREATE TABLE IF NOT EXISTS `authdata` (
              `username` VARCHAR(30) NOT NULL,
              `authToken` VARCHAR(60) NOT NULL,
              UNIQUE INDEX `token_UNIQUE` (`authToken` ASC) VISIBLE)
            ENGINE = InnoDB;
            """,
            """
            -- -----------------------------------------------------
            -- Table `GameData`
            -- -----------------------------------------------------
            DROP TABLE IF EXISTS `GameData` ;
            """,

            """
            CREATE TABLE IF NOT EXISTS `gamedata` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(30) NULL,
              `blackUsername` VARCHAR(30) NULL,
              `gameName` VARCHAR(45) NULL,
              `gameJSON` VARCHAR(3000) NOT NULL,
              PRIMARY KEY (`id`),
              UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
              UNIQUE INDEX `gameName_UNIQUE` (`gameName` ASC) VISIBLE)
            ENGINE = InnoDB;
            """,

            """
            -- -----------------------------------------------------
            -- Table `UserData`
            -- -----------------------------------------------------
            DROP TABLE IF EXISTS `UserData` ;
            """,
            """
            CREATE TABLE IF NOT EXISTS `userdata` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `username` VARCHAR(30) NOT NULL,
              `email` VARCHAR(150) NOT NULL,
              `password` VARCHAR(100) NOT NULL,
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
