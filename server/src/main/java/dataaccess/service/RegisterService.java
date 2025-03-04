package dataaccess.service;

// Import necessary classes
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.handler.RegisterRequest;
import model.*;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    // Constructor to initialize DAOs
    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Method to register a new user
    public AuthData registerUser(RegisterRequest userRequest) throws DataAccessException {
        // Validate request parameters
        if (userRequest.username() == null || userRequest.password() == null || userRequest.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        UserData userData = new UserData(userRequest.username(), userRequest.password(), userRequest.email());
        try {
            // Check if user already exists
            if (this.userDAO.isUser(userData)) {
                throw new DataAccessException("Error: already taken");
            } else {
                // Create new user and generate authentication data
                this.userDAO.createUser(userData);
                return this.authDAO.createAuth(userData);
            }
        } catch (DataAccessException e) {
            // Handle data access exceptions
            throw new DataAccessException(e.getMessage());
        }
    }
}
