package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

//implementation of the UserDAO interface. it uses a HashMap to store user data
public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> allUsers = new HashMap<>();

    // clears all user data from the in-memory storage
    @Override
    public void clear() throws DataAccessException {
        allUsers.clear();
    }

    //checks id a user exist in the in-memory storage
    @Override
    public boolean isUser(UserData userData) {
        return allUsers.containsKey(userData.username());
    }

    //retrieves a user from the in-memory storage by username
    @Override
    public UserData getUser(String username) {
        return allUsers.get(username);
    }

    //creates a new user in the in-memory storage
    @Override
    public void createUser(UserData userData) {
        //allUsers.put(userData.username(), new UserData(userData.username(), userData.password(), userData.email()));
        allUsers.put(userData.username(), userData);
    }
}
