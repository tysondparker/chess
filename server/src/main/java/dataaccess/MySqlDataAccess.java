package dataaccess;

import chess.ChessGame;
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
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();
    }

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
        executeUpdate(statement, data.username(), data.password(), data.email());
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String s -> ps.setString(i + 1, s);
                        case Integer integer -> ps.setInt(i + 1, integer);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> ps.setObject(i + 1, param);
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
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    @Override
    public int createGame(GameData data) throws DataAccessException {
        var statement = """
                INSERT INTO games (whiteUsername, blackUsername, gameName, gameState)
                VALUES (?,?,?,?)
            """;
        String json = new Gson().toJson(data.game());
        return executeUpdate(statement,data.whiteUsername(),data.blackUsername(),data.gameName(),json);
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var white = rs.getString("whiteUsername");
                        var black = rs.getString("blackUsername");
                        var name = rs.getString("gameName");
                        var json = rs.getString("gameState");
                        ChessGame game = new Gson().fromJson(json, chess.ChessGame.class);
                        return new GameData(gameId, white, black, name, game);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new SqlDataAccessException("Error : Unable to get Game: ", e);
        }
    }

    @Override
    public void updateGame(GameData gameId) throws DataAccessException {
        var statement = """
                UPDATE games
                SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ?
                WHERE gameID = ?
            """;
        String json = new Gson().toJson(gameId.game());

        executeUpdate(statement,gameId.whiteUsername(),gameId.blackUsername(),gameId.gameName(),json,gameId.gameID());

    }

    @Override
    public List<GameData> listGame() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM games"); ResultSet rs = ps.executeQuery()) {
            var list = new ArrayList<GameData>();
            var gson = new Gson();

            while(rs.next()) {
                int id = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String json = rs.getString("gameState");

                var game = gson.fromJson(json, ChessGame.class);
                list.add(new GameData(id,whiteUsername,blackUsername,gameName,game));
            }
            return list;
        } catch (SQLException e) {
            throw new SqlDataAccessException("Error : Unable to list games ", e);
        }
    }
}
