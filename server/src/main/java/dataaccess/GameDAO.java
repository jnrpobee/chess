package dataaccess;

import model.GameData;

import java.lang.reflect.Array;
import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;

    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    List<GameData> getAllGame() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;
}
