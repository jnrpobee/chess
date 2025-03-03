package server;

import com.google.gson.Gson;
import dataaccess.handler.CreateRequest;
import dataaccess.handler.LoginRequest;
import dataaccess.handler.LogoutRequest;
import dataaccess.handler.RegisterRequest;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.service.ClearService;
import dataaccess.service.LoginService;
import dataaccess.service.LogoutService;
import dataaccess.service.RegisterService;
import dataaccess.service.AuthService;
import model.AuthData;
import dataaccess.service.GameService;
import spark.*;
import dataaccess.*;
import model.*;

import java.util.Objects;

public class Server {
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ClearService clearService;
    private final AuthService authService;
    private final GameService gameService;
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();


    public Server() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        registerService = new RegisterService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        // Register handlers for each endpoint using the method reference syntax
        Spark.post("/user", this::registration);

        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);

        //list game
        Spark.post("/game", this::createGame);

        Spark.delete("db", this::clearData);
        Spark.exception(DataAccessException.class, this::exceptionHandler);


        Spark.awaitInitialization();


        return Spark.port();
    }

    private void exceptionHandler(DataAccessException e, Request request, Response response) {
        String errorMessage = e.getMessage();

        //String s = "Error: bad request";
        if (Objects.equals(errorMessage, "Error: bad request")) {
            response.status(400);
            response.body("{\"message\": \"Error: bad request\"}");
        } else if
        (Objects.equals(errorMessage, "Error: already taken")) {
            response.status(403);
            response.body("{ \"message\": \"Error: already taken\" }");
        } else if (Objects.equals(errorMessage, "Error: unauthorized")) {
            response.status(401);
            response.body("{ \"message\": \"Error: unauthorized\" }");

        }

    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public int port() {
        return Spark.port();
    }

    private Object registration(Request req, Response res) throws Exception {
        res.type("application/json");
        var user = new Gson().fromJson(req.body(), RegisterRequest.class);
        AuthData authData = registerService.registerUser(user);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }


    private Object loginUser(Request req, Response res) throws DataAccessException {
        res.type("application/json");

        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

        AuthData authData = loginService.loginUser(loginRequest);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }


    //Logs out the user represented by the authToken.
    private Object logoutUser(Request req, Response res) throws DataAccessException {
        res.type("application/json");
        var authToken = new LogoutRequest(req.headers("authorization"));

        logoutService.logoutUser(authToken);
        res.status(200);
        return "{}";
    }

    //list game


    //Create game
    private Object createGame(Request request, Response response) {
        var authToken = request.headers("authorization");
        authService.authentication(authToken);

        var createNewGame = new Gson().fromJson(request.body(), CreateRequest.class);
        GameData gameID = gameService.createGame(createNewGame);

        response.status(200);
        response.body(new Gson().toJson(gameID));
        return new Gson().toJson(gameID);
    }


    // join game


    //clear game data
    private Object clearData(Request req, Response res) throws DataAccessException {
        clearService.clearDatabase();
        res.status(200);
        return "{}";
    }

}
