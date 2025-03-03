package dataaccess.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.handler.RegisterRequest;
//import model.AuthData;
import model.*;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO)throws Exception {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AuthData registerUser(RegisterRequest userRequest) throws Exception {
        if (userRequest.username() == null || userRequest.password() == null || userRequest.email() == null) {
            throw new Exception(400, "Error: bad request");
        }

        UserData userData = new UserData(userRequest.username(), userRequest.password(), userRequest.email());
        try {
            if(this.userDAO.isUser(userData)){
                throw new Exception (403, "error: already taken");
            }else {
                this.userDAO.createUser(userData);

                return this.authDAO.createAuth(userData);
            }
        } catch (DataAccessException e} {
        throw new Exception(500, "error")
    }


    }
}
