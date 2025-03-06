package servicetest;

import dataaccess.handler.*;
import dataaccess.memory.*;
import dataaccess.service.*;
import dataaccess.DataAccessException;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class LoginServiceTest {
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final LoginService SERVICE = new LoginService(USER_DAO, AUTH_DAO);


    @BeforeEach
    void clear() throws DataAccessException {
        USER_DAO.clear();
        AUTH_DAO.clear();
    }

    @Test
    void testLoginSuccess() throws DataAccessException {
        UserData userData = new UserData("validName", "validPassword", "email@email.com");
        USER_DAO.createUser(userData);
        Assertions.assertDoesNotThrow(() -> SERVICE.loginUser(new LoginRequest(userData.username(), userData.password())));
        Assertions.assertInstanceOf(AuthData.class, SERVICE.loginUser(new LoginRequest(userData.username(), userData.password())));
    }

    @Test
    void testLoginFailure() {
        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.loginUser(new LoginRequest("invalidName", "invalidPassword")));
    }
}

