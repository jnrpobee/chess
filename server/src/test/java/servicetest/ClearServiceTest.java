package servicetest;

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
    void Clear() {
        UserData userData = new UserData("username", "password", "email@email.com");

        try {
            USER_DAO.createUser(userData);
            AuthData authData = AUTH_DAO.createAuth(userData);
            GAME_SERVICE.createGame(new CreateRequest("game"));


            Assertions.assertDoesNotThrow(() -> USER_DAO.isUser(userData));
            try {
                Assertions.assertTrue(USER_DAO.isUser(userData));
            } catch (DataAccessException ex) {
                Assertions.fail();
            }

            Assertions.assertEquals(authData, AUTH_DAO.getAuth(authData.authToken()));


            CLEAR_SERVICE.clearDatabase();


            try {
                Assertions.assertFalse(USER_DAO.isUser(userData));
            } catch (DataAccessException ex) {
                Assertions.fail();
            }
            Assertions.assertNull(AUTH_DAO.getAuth(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}



