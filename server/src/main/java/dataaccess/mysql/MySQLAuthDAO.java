package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * MySQLAuthDAO class for handling authentication data access operations.
 */
public class MySQLAuthDAO implements AuthDAO {
    private final Connection conn;

    /**
     * Constructor that initializes the database connection.
     *
     * @throws DataAccessException if there is an error configuring the database or getting the connection.
     */
    public MySQLAuthDAO() throws DataAccessException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /// Clears the AUTH table.
    /// throws DataAccessException if there is an error clearing the table.
    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE AUTH")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates a new authentication token for the given user.
     *
     * @param userData the user data. // this means that the user data is passed to the method to create a new authentication token for the given user
     * @return the created AuthData.
     * @throws DataAccessException if there is an error creating the authentication token.
     */
    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO AUTH (NAME, TOKEN) VALUE (?, ?)")) {
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, authToken);

            preparedStatement.executeUpdate();

            return new AuthData(authToken, userData.username());
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Retrieves the authentication data for the given token.
     *
     * @param authToken the authentication token.
     *                  // this means that the authentication token is passed to
     *                  the method to retrieve the authentication data for the given token
     * @return the retrieved AuthData.
     * @throws DataAccessException if there is an error retrieving the authentication data.
     */
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT NAME from AUTH where TOKEN=?")) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                String username = "";
                while (rs.next()) {
                    username = rs.getString("NAME");
                }
                if (Objects.equals(username, "")) {
                    throw new DataAccessException("invalid authorization token");
                }
                return new AuthData(authToken, username);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Deletes the authentication token for the given username.
     *
     * @param username the username.
     *                 // this means that the username is passed to the method to delete the
     *                 authentication token for the given username
     * @return true if the token was deleted, false otherwise.
     * @throws DataAccessException if there is an error deleting the authentication token.
     */
    @Override
    public boolean deleteAuth(String username) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("DELETE FROM AUTH WHERE TOKEN=?")) {
            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Checks if the authentication token exists.
     *
     * @param authToken the authentication token.
     * @return true if the token exists, false otherwise.
     * @throws DataAccessException if there is an error checking the token.
     */
    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT TOKEN FROM AUTH WHERE TOKEN=?")) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
