package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class SQLUserDAO extends SQL_DAO implements UserDAO {


    @Override
    public void createUser(UserData u) throws DataAccessException {
        try {
            var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
            String username = u.username();
            String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
            String email = u.email();
            executeUpdate(statement, username, hashedPassword, email);
        } catch (DataAccessException e) {
            throw new DataAccessException(500,e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM UserData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE UserData";
        executeUpdate(statement);
    }
}
