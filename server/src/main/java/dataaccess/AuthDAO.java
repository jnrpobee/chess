package dataaccess;

import dataaccess.handler.LoginRequest;
import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void clear() throws DataAccessException;

    AuthData createAuth(UserData userData) throws DataAccessException;

    LoginRequest getAuth(String authToken) throws DataAccessException;

    boolean deleteAuth(String username) throws DataAccessException;
}
