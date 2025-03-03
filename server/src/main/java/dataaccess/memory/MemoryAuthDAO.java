package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authTokens = new HashMap<>();


    public void clear() {
        authTokens.clear();


    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        authTokens.put(userData.username(), authData);
        return authData;

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public boolean deleteAuth(String username) throws DataAccessException {
        if (authTokens.containsKey(username)) {
            authTokens.remove(username);
            return true;
        } else {
            return false;
        }
    }
}
