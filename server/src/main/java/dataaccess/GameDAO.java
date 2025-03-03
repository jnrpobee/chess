package dataaccess;

import model.GameData;

public interface GameDAO {
    void clear() throws DataAccessException;

    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;
}
