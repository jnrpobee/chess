package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.handler.LoginRequest;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    // Constructor to initialize DAOs
    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Method to handle user login
    public AuthData loginUser(LoginRequest loginRequest) throws DataAccessException {
        // Retrieve user data from the database
        UserData userData = this.userDAO.getUser(loginRequest.username());
        if (userData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Delete existing authentication data for the user
        //this.authDAO.deleteAuth(userData.username());

        // Check if the provided password matches the stored password
        if (Objects.equals(userData.password(), loginRequest.password())) {
            // Create new authentication data and return it
            return this.authDAO.createAuth(userData);
        } else if (BCrypt.checkpw(loginRequest.password(), userData.password())) {
            return this.authDAO.createAuth(userData);
        } else {
            throw new DataAccessException("Error: unauthorized");
        }

    }
}
