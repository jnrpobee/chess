package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    /**
     * Clears all authentication data and DataAccessException if there is an error during the operation.
     */
    void clear() throws DataAccessException;

    /**
     * Creates a new authentication token for the given user.
     * the userData create the auth token.
     *
     * @return the created AuthData.
     * throws DataAccessException if there is an error during the operation.
     */
    AuthData createAuth(UserData userData) throws DataAccessException;

    /**
     * Retrieves authentication data for the given auth token.
     * authToken the auth token to look up.
     * return the corresponding AuthData.
     * throws DataAccessException if there is an error during the operation.
     */
    AuthData getAuth(String authToken) throws DataAccessException;

    /**
     * Deletes the authentication data for the given username.
     * username the username whose auth data should be deleted.
     * return true if the deletion was successful, false otherwise.
     * throws DataAccessException if there is an error during the operation.
     */
    boolean deleteAuth(String username) throws DataAccessException;

    /**
     * Checks if an authentication token exists.
     * authToken the auth token to check.
     * return true if the auth token exists, false otherwise.
     * throws DataAccessException if there is an error during the operation.
     */
    boolean authExists(String authToken) throws DataAccessException;
}
