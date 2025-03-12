package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dataaccess.handler.*;
import dataaccess.mySQL.MySQLAuthDAO;
import dataaccess.service.*;
import dataaccess.memory.*;
import spark.*;
import dataaccess.*;
import model.*;
import model.AuthData;
import result.GameDataResult;


import java.util.List;
import java.util.Objects;


public class Server {
    //Service instances
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ClearService clearService;
    private final GameService gameService;

    //DAO instances
    UserDAO userDAO = new MemoryUserDAO();
    //AuthDAO authDAO = new MemoryAuthDAO();
    AuthDAO authDAO;

    {
        try {
            authDAO = new MySQLAuthDAO();
        } catch (DataAccessException ex) {
            System.out.printf("Unable to connect to database: %s%n", ex.getMessage());
        }
    }

    //Constructor to initialize services
    public Server() {

        GameDAO gameDAO = new MemoryGameDAO();
        registerService = new RegisterService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(gameDAO, authDAO);

    }

    //Methods to run the server
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        // Register handlers for each endpoint using the method reference syntax
        Spark.post("/user", this::registration);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::getGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("db", this::clearData);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    // Exception handler for DataAccessException
    private void exceptionHandler(DataAccessException e, Request request, Response response) {
        String errorMessage = e.getMessage();

        if (Objects.equals(errorMessage, "Error: bad request")) {
            response.status(400);
            response.body("{\"message\": \"Error: bad request\"}");
        } else if (Objects.equals(errorMessage, "Error: already taken")) {
            response.status(403);
            response.body("{ \"message\": \"Error: already taken\" }");
        } else if (Objects.equals(errorMessage, "Error: unauthorized")) {
            response.status(401);
            response.body("{ \"message\": \"Error: unauthorized\" }");
        } else if (Objects.equals(errorMessage, "Error: (description of error)")) {
            response.status(500);
            response.body(new Gson().toJson(new FailureResponse(e.getMessage())));
        }
    }

    // Method to stop the server
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    // Method to get the current port
    public int port() {
        return Spark.port();
    }

    // Handler for user registration
    private Object registration(Request request, Response response) throws Exception {
        response.type("application/json");
        var user = new Gson().fromJson(request.body(), RegisterRequest.class);
        AuthData authData = registerService.registerUser(user);

        response.status(200);
        response.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    // Handler for user login
    private Object loginUser(Request request, Response response) throws DataAccessException {
        response.type("application/json");
        var loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        AuthData authData = loginService.loginUser(loginRequest);

        response.status(200);
        return new Gson().toJson(authData);
    }

    // Handler for user logout
    private Object logoutUser(Request request, Response response) {
        try {
            response.type("application/json");
            var authToken = new LogoutRequest(request.headers("authorization"));

            logoutService.logoutUser(authToken);
            response.status(200);
            JsonElement successResponse = new Gson().toJsonTree(new Object());
            return new Gson().toJson(successResponse);
        } catch (DataAccessException e) {
            exceptionHandler(e, request, response);
            FailureResponse failureResponse = new FailureResponse(e.getMessage());
            return new Gson().toJson(failureResponse);
        }
    }

    // Handler to list games
    private Object getGames(Request request, Response response) {
        try {
            response.type("application/json");
            var authToken = request.headers("authorization");
            gameService.authentication(authToken);

            List<GameDataResult> allGames = gameService.listGame();
            response.status(200);
            return new Gson().toJson(new ListRequest(allGames));
        } catch (DataAccessException e) {
            exceptionHandler(e, request, response);
            FailureResponse failureResponse = new FailureResponse(e.getMessage());
            return new Gson().toJson(failureResponse);
        }
    }


    // Handler to create a new game
    private Object createGame(Request request, Response response) {
        try {
            var authToken = request.headers("authorization");
            gameService.authentication(authToken);

            var createNewGame = new Gson().fromJson(request.body(), CreateRequest.class);
            GameData gameID = gameService.createGame(createNewGame);

            response.status(200);
            response.body(new Gson().toJson(gameID));
            return new Gson().toJson(gameID);
        } catch (DataAccessException e) {
            exceptionHandler(e, request, response);
            FailureResponse failureResponse = new FailureResponse(e.getMessage());
            return new Gson().toJson(failureResponse);
        }
    }


    // Handler to join an existing game
    private Object joinGame(Request request, Response response) throws DataAccessException {
        var authToken = request.headers("authorization");
        gameService.authentication(authToken);

        AuthData authData = gameService.getAuthData(authToken);
        var joinData = new Gson().fromJson(request.body(), JoinRequest.class);
        gameService.joinGame(joinData, authData);

        response.status(200);
        return "{}";
    }

    // Handler to clear game data
    private Object clearData(Request request, Response response) throws DataAccessException {
        clearService.clearDatabase();
        response.status(200);
        return "{}";
    }
}
