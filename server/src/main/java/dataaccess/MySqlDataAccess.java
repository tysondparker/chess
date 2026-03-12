package dataaccess;

import com.google.gson.Gson;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.SqlDataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new SqlDataAccessException("Unable to get User: ", e);
        }
    }

    public UserData readUser (ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username,password,email);
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashed = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        executeUpdate(statement, data.username(), hashed, data.email());
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String) {
                        ps.setString(i + 1, (String) param);
                    } else if (param instanceof Integer) {
                        ps.setInt(i + 1, (Integer) param);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    } else {
                        ps.setObject(i + 1, param);
                    }
                }

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
                return 0;
        } catch (SQLException e) {
            throw new SqlDataAccessException("unable to update database ", e);
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?,?)";
        executeUpdate(statement, authData.authToken(),authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),rs.getString("username"));
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new SqlDataAccessException("Unable to get User: ", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("DELETE FROM auth");
        executeUpdate("DELETE FROM games");
        executeUpdate("DELETE FROM users");
    }

    @Override
    public void deleteAuth(String authData) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authData);
    }

    @Override
    public int createGame(GameData data) throws DataAccessException {
        var statement = """
                INSERT INTO games (whiteUsername, blackUsername, gameName, gameState)
                VALUES (?,?,?,?)
            """;
        String json = new Gson().toJson(data);
        return executeUpdate(data.whiteUsername(),data.blackUsername(),data.gameName(),json);
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameId) throws DataAccessException {

    }

    @Override
    public List<GameData> listGame() throws DataAccessException {
        return List.of();
    }
}
