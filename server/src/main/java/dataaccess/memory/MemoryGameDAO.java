package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> gameList = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        gameList.clear();
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        var gameId = gameData.gameID();
        gameList.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameList.get(gameID);
    }

    @Override
    public List<GameData> getAllGame() throws DataAccessException {
        return (List<GameData>) gameList.values();
    }


    //public List<GameData>

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        gameList.put(gameData.gameID(), gameData);
    }
}
