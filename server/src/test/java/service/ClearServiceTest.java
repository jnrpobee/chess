package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.handler.CreateRequest;
import dataaccess.memory.*;
import dataaccess.service.ClearService;
import dataaccess.service.GameService;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClearServiceTest {
    static final UserDAO USER_DAO = new MemoryUserDAO();
    static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final GameDAO GAME_DAO = new MemoryGameDAO();
    static final GameService GAME_SERVICE = new GameService(GAME_DAO, AUTH_DAO);
    static final ClearService CLEAR_SERVICE = new ClearService(USER_DAO, AUTH_DAO, GAME_DAO);


    @Test
    void clear() {
        // Test clearing the database
        UserData userData = new UserData("username", "password", "email@email.com");

        try {
            // Create a user and authenticate
            USER_DAO.createUser(userData);
            AuthData authData = AUTH_DAO.createAuth(userData);
            // Create a game
            GAME_SERVICE.createGame(new CreateRequest("game"));

            // Assert that the user exists in the database
            Assertions.assertDoesNotThrow(() -> USER_DAO.isUser(userData));
            try {
                Assertions.assertTrue(USER_DAO.isUser(userData));
            } catch (DataAccessException ex) {
                Assertions.fail();
            }

            // Assert that the authentication data is correct
            Assertions.assertEquals(authData, AUTH_DAO.getAuth(authData.authToken()));

            // Clear the database
            CLEAR_SERVICE.clearDatabase();

            // Assert that the user no longer exists in the database
            try {
                Assertions.assertFalse(USER_DAO.isUser(userData));
            } catch (DataAccessException ex) {
                Assertions.fail();
            }
            // Assert that the authentication data is null
            Assertions.assertNull(AUTH_DAO.getAuth(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}



