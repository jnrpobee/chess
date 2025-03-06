package service;

import dataaccess.DataAccessException;
import dataaccess.handler.LogoutRequest;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.service.LogoutService;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogoutServiceTest {
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final LogoutService SERVICE = new LogoutService(AUTH_DAO);

    @BeforeEach
    void clear() {
        AUTH_DAO.clear();
    }

    @Test
    void logoutUserPositive() {
        // Test logging out a user successfully
        UserData userData = new UserData("validUser", "validPassword", "email@email.com");

        AuthData authData = null;
        authData = AUTH_DAO.createAuth(userData);

        // Test that a user currently logged in can log out
        AuthData finalAuthData = authData;
        Assertions.assertDoesNotThrow(() -> SERVICE.logoutUser(new LogoutRequest(finalAuthData.authToken())));
    }

    @Test
    void logoutUserNegative() {
        // Test logging out with an invalid token
        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.logoutUser(new LogoutRequest("1000")));
    }
}