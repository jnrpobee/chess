package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;


    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }
}
