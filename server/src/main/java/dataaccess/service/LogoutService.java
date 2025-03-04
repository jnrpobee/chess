package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.handler.LogoutRequest;
import model.AuthData;

/**
 * Service to handle user logout operations.
 */
public class LogoutService {
    private final AuthDAO authDAO;

    // Constructor to initialize AuthDAO
    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    /**
     * Logs out a user by invalidating their authentication token.
     *
     * @param authToken the authentication token to be invalidated
     * @throws DataAccessException if the token is invalid or an error occurs during the process
     */
    public void logoutUser(LogoutRequest authToken) throws DataAccessException {
        try {
            // Retrieve authentication data using the provided token
            AuthData authData = authDAO.getAuth(authToken.authToken());
            if (authData == null) {
                throw new DataAccessException("Error: unauthorized");
            }
            
            // Attempt to delete the authentication token
            boolean removed = authDAO.deleteAuth(authData.authToken());
            if (!removed) {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            // Handle any exceptions that occur during the logout process
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
