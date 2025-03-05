package servicetest;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.service.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {

    // static final LogoutService SERVICE = new LogoutService(AUTH_DAO);

    @BeforeEach
    void clear() throws DataAccessException {
    }

    @Test
    void logoutUserPositive() {
    }

    @Test
    void logoutUserNegative() {
    }
}