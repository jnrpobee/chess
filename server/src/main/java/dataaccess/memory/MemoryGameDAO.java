package dataaccess.memory;

import dataaccess.GameDAO;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> gameList = new HashMap<>();
}
