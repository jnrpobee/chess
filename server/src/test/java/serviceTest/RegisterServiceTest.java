package serviceTest;

import dataaccess.*;
import dataaccess.handler.RegisterRequest;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterServiceTest {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();
    static final RegisterService service;

    static {
        try {
            service = new RegisterService(userDAO, authDAO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }


    @Test
    void testRegisterUserPass() {
        RegisterRequest goodRequest = new RegisterRequest(
                "Name", "Password", "email@email.com");

        Assertions.assertDoesNotThrow(() -> service.registerUser(goodRequest));

    }

    @Test
    void testRegisterUserFail() {
        RegisterRequest wrongRequest = new RegisterRequest(
                null, "invalid Password", " invalid Email"
        );

        Assertions.assertThrows(DataAccessException.class, () -> service.registerUser(wrongRequest));
    }
}
