package dataaccess;

import dataaccess.mysql.MySQLAuthDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    private static final AuthDAO authDAO;

    static {
        try {
            authDAO = new MySQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void positiveClear() {
        UserData userData = new UserData(
                "name",
                "password",
                "email@email.com");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.deleteAuth(authData.authToken()));

            Assertions.assertDoesNotThrow(authDAO::clear);

            Assertions.assertFalse(authDAO.authExists(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void positiveCreateAuth() {
        UserData userData = new UserData(
                "name",
                "password",
                "email@email.com");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.authExists(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeCreateAuth() {
        UserData invalidUserData = new UserData(
                null,
                "password",
                "email@email.com"
        );
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(invalidUserData));
    }

    @Test
    void positiveGetAuth() {
        UserData userData = new UserData(
                "name",
                "password",
                "email@email.com");
        try {
            var authData = authDAO.createAuth(userData);

            Assertions.assertEquals(authData, authDAO.getAuth(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeGetAuth() {
        UserData userData = new UserData(
                "name",
                "password",
                "email@email.com");
        try {
            authDAO.createAuth(userData);
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("random"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void positiveDeleteAuth() {
        UserData userData = new UserData(
                "name",
                "password",
                "email@email.com");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.deleteAuth(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeDeleteAuth() {
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(
                "random"));
    }

}
