package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

/**
 * Service class to clear the database.
 */
public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    /**
     * Constructor to initialize DAOs.
     *
     * @param userDAO Data Access Object for users
     * @param authDAO Data Access Object for authentication
     * @param gameDAO Data Access Object for games
     */
    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Clears all data from the database.
     *
     * @throws DataAccessException if there is an error during the clearing process
     */
    public void clearDatabase() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
