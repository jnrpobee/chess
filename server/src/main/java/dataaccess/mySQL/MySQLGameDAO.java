package dataaccess.mySQL;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
        try (var preparedStatement = conn.prepareStatement("INSERT INTO GAME (ID, WHITENAME, BLACKNAME, GAMENAME, JSON) VALUES(?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, String.valueOf(gameData.gameID()));
            preparedStatement.setString(2, gameData.whiteUsername());
            preparedStatement.setString(3, gameData.blackUsername());
            preparedStatement.setString(4, gameData.gameName());
            preparedStatement.setString(5, new Gson().toJson(gameData.game()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT * from GAME where ID=?")) {
            preparedStatement.setString(1, String.valueOf(gameID));
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    var whiteUsername = rs.getString("WHITENAME");
                    var blackUsername = rs.getString("BLACKNAME");
                    var gameName = rs.getString("GAMENAME");
                    var game = new Gson().fromJson(
                            rs.getString("JSON"),
                            ChessGame.class
                    );

                    return new GameData(
                            gameID,
                            whiteUsername,
                            blackUsername,
                            gameName,
                            game
                    );
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Collection<GameData> getAllGame() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();

        try (var preparedStatement = conn.prepareStatement("SELECT * from GAME")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    int gameID = rs.getInt("ID");
                    var whiteUsername = rs.getString("WHITENAME");
                    var blackUsername = rs.getString("BLACKNAME");
                    var gameName = rs.getString("GAMENAME");
                    var game = new Gson().fromJson(
                            rs.getString("JSON"),
                            ChessGame.class
                    );

                    gameList.add(new GameData(
                            gameID,
                            whiteUsername,
                            blackUsername,
                            gameName,
                            game
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return gameList;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement(
                "UPDATE GAME SET WHITENAME=?, BLACKNAME=?, GAMENAME=?, JSON=? WHERE ID=?")) {
            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, new Gson().toJson(gameData.game()));
            preparedStatement.setString(5, String.valueOf(gameData.gameID()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

