package dataaccess;

import dataaccess.mysql.MySQLAuthDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    private static final AuthDAO AUTH_DAO;

    static {
        try {
            AUTH_DAO = new MySQLAuthDAO();
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
            var authData = AUTH_DAO.createAuth(userData);
            Assertions.assertTrue(AUTH_DAO.deleteAuth(authData.authToken()));

            Assertions.assertDoesNotThrow(AUTH_DAO::clear);

            Assertions.assertFalse(AUTH_DAO.authExists(authData.authToken()));
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
            var authData = AUTH_DAO.createAuth(userData);
            Assertions.assertTrue(AUTH_DAO.authExists(authData.authToken()));
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
        Assertions.assertThrows(DataAccessException.class, () -> AUTH_DAO.createAuth(invalidUserData));
    }

    @Test
    void positiveGetAuth() {
        UserData userData = new UserData(
                "name",
                "password",
                "email@email.com");
        try {
            var authData = AUTH_DAO.createAuth(userData);

            Assertions.assertEquals(authData, AUTH_DAO.getAuth(authData.authToken()));
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
            AUTH_DAO.createAuth(userData);
            Assertions.assertThrows(DataAccessException.class, () -> AUTH_DAO.getAuth("random"));
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
            var authData = AUTH_DAO.createAuth(userData);
            Assertions.assertTrue(AUTH_DAO.deleteAuth(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeDeleteAuth() {
        Assertions.assertDoesNotThrow(() -> AUTH_DAO.deleteAuth(
                "random"));
    }

    @Test
    void positiveAuthExists() {
        UserData userData = new UserData(
                "name",
                "password",
                "email");
        try {
            Assertions.assertTrue(AUTH_DAO.authExists(AUTH_DAO.createAuth(userData).authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeAuthExist() {
        try {
            Assertions.assertFalse(AUTH_DAO.authExists(
                    "randomToken"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}
