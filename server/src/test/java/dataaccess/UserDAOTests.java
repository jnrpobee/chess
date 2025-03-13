package dataaccess;

import dataaccess.mysql.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserDAOTests {
    private static final UserDAO userDAO;

    static {
        try {
            userDAO = new MySQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void positiveClear() {
        Assertions.assertDoesNotThrow(userDAO::clear);
        try {
            Assertions.assertFalse(userDAO.isUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            )));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void positiveIsUser() {
        try {
            userDAO.createUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            ));
        } catch (DataAccessException ignored) {
        }
        Assertions.assertDoesNotThrow(() -> userDAO.isUser(new UserData(
                "name",
                "password",
                "email@email.com"
        )));
        try {
            Assertions.assertTrue(userDAO.isUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            )));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeIsUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.isUser(new UserData(
                "wrongName",
                "wrongPassword",
                "wrongEmail@email.com"
        )));
        try {
            Assertions.assertFalse(userDAO.isUser(new UserData(
                    "badName",
                    "badPass",
                    "badEmail@email.com"
            )));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void positiveCreateUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(new UserData(
                "name",
                "password",
                "email@email.com")));
    }

    @Test
    void negativeCreateUser() {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData(
                null,
                null,
                null
        )));
    }

    @Test
    void positiveGetUser() {
        try {
            userDAO.createUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            ));
        } catch (DataAccessException ignored) {
        }
        try {
            Assertions.assertEquals(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            ), userDAO.getUser("name"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeGetUser() {
        try {
            Assertions.assertNull(userDAO.getUser("wrongName"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

}
