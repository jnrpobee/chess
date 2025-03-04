package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * clears all user from the data source.
     * throws DataAccessException if an error occurs during the operation
     **/
    void clear() throws DataAccessException;

    /**
     * checks if the given user exists in the data source
     * param userData: the user data to check
     * throws DataAccessException if an error occurs during the operation
     **/
    boolean isUser(UserData userData) throws DataAccessException;

    /**
     * creates a new user in the data source
     * param userData: the user data to create.
     * throws DataAccessException if an error occurs during the operation
     **/
    void createUser(UserData userData) throws DataAccessException;

    /**
     * Retrieves user data from the given username
     * param username: the username of the user to retrieve
     * return the user data for the given username
     * throws DataAccessException if an error occurs during the operation
     **/
    UserData getUser(String username) throws DataAccessException;
}
