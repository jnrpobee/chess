package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;

    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    Collection<GameData> getAllGame() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;
}
