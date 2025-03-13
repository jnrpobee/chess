package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * MySQLUserDAO class for handling user data access operations.
 */
public class MySQLUserDAO implements UserDAO {
    private final Connection conn;

    /**
     * Constructor that initializes the database connection.
     * @throws DataAccessException if there is an error configuring the database or getting the connection.
     */
    public MySQLUserDAO() throws DataAccessException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Clears the USERS table.
     * @throws DataAccessException if there is an error clearing the table.
     */
    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE USERS")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Checks if the user exists in the USERS table.
     *
     * @param userData the user data. // this means that the user data is passed to the method to check if the user exists in the USERS table
     * @return true if the user exists, false otherwise.
     * @throws DataAccessException if there is an error checking the user.
     */
    @Override
    public boolean isUser(UserData userData) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT NAME FROM USERS WHERE NAME=?")) {
            preparedStatement.setString(1, userData.username());
            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    /**
     * Creates a new user in the USERS table.
     * @param userData the user data. // this means that the user data is passed to the method to create the user in the USERS table
     * @throws DataAccessException if there is an error creating the user.
     */
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("INSERT INTO USERS (NAME, PASSWORD, EMAIL) VALUE(?, ?, ?)")) {
            preparedStatement.setString(1, userData.username());
            String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, userData.email());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Retrieves the user data for the given username.
     * @param username the username. // this means that the username is the primary key in the USERS table
     * @return the retrieved UserData. // this means that the UserData object is returned if the user exists
     * @throws DataAccessException if there is an error retrieving the user data.
     */
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT PASSWORD, EMAIL from USERS where NAME=?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                String password = "";
                String email = "";
                while (rs.next()) {
                    password = rs.getString("PASSWORD");
                    email = rs.getString("EMAIL");
                }
                if (Objects.equals(password, "") && Objects.equals(email, "")) {
                    return null;
                }
                return new UserData(username, password, email);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
