package servicetest;

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
        USER_DAO.clear();
        AUTH_DAO.clear();
    }


    @Test
    void testRegisterUserPass() {
        RegisterRequest goodRequest = new RegisterRequest(
                "Name", "Password", "email@email.com");

        Assertions.assertDoesNotThrow(() -> SERVICE.registerUser(goodRequest));

    }

    @Test
    void testRegisterUserFail() {
        RegisterRequest wrongRequest = new RegisterRequest(
                null, "invalid Password", " invalid Email"
        );

        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.registerUser(wrongRequest));
    }


    // // Test login service with valid credentials
    // @BeforeEach
    // void clear() {
    //     userDAO.clear();
    //     authDAO.clear();
    // }

    // @Test
    // void testLoginUserPass() {
    //     AssertionError.assertDoesNotThrow(() -> {
    //         RegisterRequest goodRequest = new RegisterRequest(
    //                 "Name", "Password", "
}
