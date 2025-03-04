package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

//implementation of the GameDAO interface. it uses a HashMap to store game data
public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> gameList = new HashMap<>();

    // clears all game data from the in-memory storage
    @Override
    public void clear() throws DataAccessException {
        gameList.clear();
    }

    //checks if a game exist in the in-memory storage
    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        gameList.put(gameData.gameID(), gameData);
    }

    //retrieves a game from the in-memory storage by gameID
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameList.get(gameID);
    }

    //retrieves all games from the in-memory storage
    @Override
    public Collection<GameData> getAllGame() throws DataAccessException {
        return gameList.values();
    }

    //updates a game in the in-memory storage
    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        gameList.put(gameData.gameID(), gameData);
    }
}
