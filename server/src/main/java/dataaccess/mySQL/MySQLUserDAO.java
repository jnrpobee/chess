package dataaccess.mySQL;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

public class MySQLUserDAO implements UserDAO {
    private final Connection conn;

    public MySQLUserDAO() throws DataAccessException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE USERS")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

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

    
}
