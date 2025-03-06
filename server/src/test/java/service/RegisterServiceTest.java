package service;

import dataaccess.*;
import dataaccess.handler.RegisterRequest;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterServiceTest {
    static final UserDAO USER_DAO = new MemoryUserDAO();
    static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final RegisterService SERVICE;

    //static final LoginService service = new LoginService(userDAO, authDAO);

    static {
        try {
            SERVICE = new RegisterService(USER_DAO, AUTH_DAO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        // Clear the data before each test to ensure a clean state
        USER_DAO.clear();
        AUTH_DAO.clear();
    }


    @Test
    void testRegisterUserPass() {
        // Test registering a user with valid details
        RegisterRequest goodRequest = new RegisterRequest(
                "Name", "Password", "email@email.com");

        // Assert that no exception is thrown during registration
        Assertions.assertDoesNotThrow(() -> SERVICE.registerUser(goodRequest));
    }

    @Test
    void testRegisterUserFail() {
        // Test registering a user with invalid details
        RegisterRequest wrongRequest = new RegisterRequest(
                null, "invalid Password", " invalid Email"
        );

        // Assert that a DataAccessException is thrown during registration
        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.registerUser(wrongRequest));
    }

}
