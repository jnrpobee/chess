package dataaccess.service;

import dataaccess.AuthDAO;

/**
 * logout a user
 */
public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }
}
