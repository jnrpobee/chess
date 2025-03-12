package dataaccess.mySQL;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {
    private final Connection connect;


    public static void configureDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
            }
        } catch (DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }


    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
        try {
            connect = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public boolean deleteAuth(String username) throws DataAccessException {
        return false;
    }

    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        return false;
    }
}
