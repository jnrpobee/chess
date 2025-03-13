package dataaccess.mysql;

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

/**
 * MySQLGameDAO class for handling game data access operations.
 */
public class MySQLGameDAO implements GameDAO {
    private final Connection conn;

    /**
     * Constructor that initializes the database connection.
     * @throws DataAccessException if there is an error configuring the database or getting the connection.
     */
    public MySQLGameDAO() throws DataAccessException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Clears the GAME table.
     * @throws DataAccessException if there is an error clearing the table.
     */
    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE GAME")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Adds a new game to the GAME table.
     * @param gameData the game data. // this means that the game data is passed to the method to add a new game to the GAME table 
     * @throws DataAccessException if there is an error adding the game.
     */
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

    /**
     * Retrieves the game data for the given game ID.
     * @param gameID the game ID. // this means that the game ID is passed to the method to retrieve the game data for the given game ID 
     * @return the retrieved GameData.
     * @throws DataAccessException if there is an error retrieving the game data.
     */
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

    /**
     * Retrieves all games from the GAME table.
     * @return a collection of all GameData. // this means that a collection of all GameData objects is returned  
     * @throws DataAccessException if there is an error retrieving the games.
     */
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

    /**
     * Updates the game data for the given game ID.
     * @param gameData the game data. // this means that the game data is passed to the method to update the game data for the given game ID
     * @throws DataAccessException if there is an error updating the game data.
     */
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

