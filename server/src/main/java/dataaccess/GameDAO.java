package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    // Clear all game data  from the data source
    void clear() throws DataAccessException;

    // Adds a new game to the source
    void addGame(GameData gameData) throws DataAccessException;

    // Retrieves a game by its ID
    GameData getGame(int gameID) throws DataAccessException;

    // Retrieves all games from the data source
    Collection<GameData> getAllGame() throws DataAccessException;

    // Updates an existing game in the data source
    void updateGame(GameData gameData) throws DataAccessException;
}
