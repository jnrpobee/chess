package dataaccess.memory;

import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> allUsers = new HashMap<>();
}
