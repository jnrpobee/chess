package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.handler.LogoutRequest;
import model.AuthData;

/**
 * logout a user
 */
public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }


    public void logoutUser(LogoutRequest authToken) throws DataAccessException {
        try {
            AuthData authData = authDAO.getAuth(authToken.authToken());
            if (authData == null) {
                throw new DataAccessException("Error: unauthorized");
            }
            boolean removed = authDAO.deleteAuth(authData.authToken());
            if (!removed) {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
