package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO extends SqlDaoBase implements UserDAO {


    @Override
    public void createUser(UserData u) throws DataAccessException {
        try {
            var statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
            String username = u.username();
            String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
            String email = u.email();

            executeUpdate(statement, username, hashedPassword, email);

        } catch (DataAccessException e) {
            throw new DataAccessException(500,e.getMessage());
        }
    }

    @Override
    public boolean verifyPassword(String cleanPassword, String hashedPassword){
        return BCrypt.checkpw(cleanPassword,hashedPassword);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userdata WHERE username=?";
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

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var hashedPassword = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, hashedPassword,email);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE userdata";
        executeUpdate(statement);
    }
}
