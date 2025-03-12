package dataaccess.mySQL;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    private final Connection conn;


    public MySQLGameDAO() throws DataAccessException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE GAME")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {

    }

    @Override
    public Collection<GameData> getAllGame() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }
}
