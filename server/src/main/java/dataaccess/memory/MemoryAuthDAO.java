package dataaccess.memory;

import dataaccess.AuthDAO;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthDAO> authTokens = new HashMap<>();


    public void clear() {
        authTokens.clear();


    }
}
