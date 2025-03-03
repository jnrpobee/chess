package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.handler.LoginRequest;
import model.AuthData;
import model.UserData;

import java.util.Objects;

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

    public AuthData loginUser(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = this.userDAO.getUser(loginRequest.username());
        if (userData == null) {
            throw new DataAccessException("error: unauthorized");
        }
        boolean removed = this.authDAO.deleteAuth(userData.username());
        if (Objects.equals(userData.password(), loginRequest.password())) {
            return this.authDAO.createAuth(userData);
        } else {
            throw new DataAccessException("Error: unauthorized");
        }

    }

    public String getUser(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken).username();
    }

}
