package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.handler.LoginRequest;
import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthDAO> authTokens = new HashMap<>();


    public void clear() {
        authTokens.clear();


    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        return null;
    }

    @Override
    public LoginRequest getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public boolean deleteAuth(String username) throws DataAccessException {
        return false;
    }
}
