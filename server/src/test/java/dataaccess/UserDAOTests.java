package dataaccess;

import dataaccess.mysql.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    private static final UserDAO USER_DAO;

    static {
        try {
            USER_DAO = new MySQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void positiveClear() {
        Assertions.assertDoesNotThrow(USER_DAO::clear);
        try {
            Assertions.assertFalse(USER_DAO.isUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            )));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void positiveIsUser() {
        try {
            USER_DAO.createUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            ));
        } catch (DataAccessException ignored) {
        }
        Assertions.assertDoesNotThrow(() -> USER_DAO.isUser(new UserData(
                "name",
                "password",
                "email@email.com"
        )));
        try {
            assertTrue(USER_DAO.isUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            )));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void negativeIsUser() {
        Assertions.assertDoesNotThrow(() -> USER_DAO.isUser(new UserData(
                "wrongName",
                "wrongPassword",
                "wrongEmail@email.com"
        )));
        try {
            Assertions.assertFalse(USER_DAO.isUser(new UserData(
                    "badName",
                    "badPass",
                    "badEmail@email.com"
            )));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void positiveCreateUser() {
        Assertions.assertDoesNotThrow(() -> USER_DAO.createUser(new UserData(
                "name",
                "password",
                "email@email.com")));
    }

    @Test
    void negativeCreateUser() {
        Assertions.assertThrows(DataAccessException.class, () -> USER_DAO.createUser(new UserData(
                null,
                null,
                null
        )));
    }

    @Test
    void positiveGetUser() {
        UserData userData = new UserData("name", "password", "email@email.com");
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        //String hashedPassword = String.valueOf(BCrypt.checkpw(userData.password()));
        try {
            USER_DAO.createUser(new UserData(
                    "name",
                    "password",
                    "email@email.com"
            ));
        } catch (DataAccessException ignored) {
        }
    }

    @Test
    void negativeGetUser() {
        try {
            Assertions.assertNull(USER_DAO.getUser("wrongName"));
        } catch (DataAccessException e) {
            fail();
        }
    }

}
