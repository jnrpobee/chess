package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> allUsers = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        allUsers.clear();
    }

    @Override
    public boolean isUser(UserData userData) {
        return allUsers.containsKey(userData.username());
    }

    @Override
    public UserData getUser(String username) {
        return allUsers.get(username);
    }

    @Override
    public void createUser(UserData userData) {
        allUsers.put(userData.username(), userData);
    }
}
