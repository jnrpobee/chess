package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;
//implementation of the AuthDAO interface. it uses a HashMap to store auth data
public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    // clears all auth data from the in-memory storage
    public void clear() {
        authTokens.clear();
    }

    //creates a new auth data in the in-memory storage
    @Override
    public AuthData createAuth(UserData userData) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    //retrieves a auth data from the in-memory storage by authToken
    @Override
    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }
    
    //deletes a auth data from the in-memory storage by username
    @Override
    public boolean deleteAuth(String username) {
        if (authTokens.containsKey(username)) {
            authTokens.remove(username);
            return true;
        } else {
            return false;
        }
    }

    //checks if a auth data exist in the in-memory storage
    @Override
    public boolean authExists(String authToken) {
        return authTokens.containsKey(authToken);
    }
}
