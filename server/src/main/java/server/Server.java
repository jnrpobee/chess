package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dataaccess.handler.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.service.ClearService;
import dataaccess.service.LoginService;
import dataaccess.service.LogoutService;
import dataaccess.service.RegisterService;
import model.AuthData;
import dataaccess.service.GameService;
import result.GameDataResult;
import spark.*;
import dataaccess.*;
import model.*;

import java.util.List;
import java.util.Objects;


public class Server {
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ClearService clearService;
    private final GameService gameService;
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();


    public Server() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        registerService = new RegisterService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(gameDAO, authDAO);
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
        Spark.get("/game", this::getGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("db", this::clearData);
        Spark.exception(DataAccessException.class, this::exceptionHandler);


        Spark.awaitInitialization();


        return Spark.port();
    }

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


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public int port() {
        return Spark.port();
    }

    private Object registration(Request request, Response response) throws Exception {
        response.type("application/json");
        var user = new Gson().fromJson(request.body(), RegisterRequest.class);
        AuthData authData = registerService.registerUser(user);

        response.status(200);
        response.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }


    private Object loginUser(Request request, Response response) throws DataAccessException {
        response.type("application/json");

        var loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);

        AuthData authData = loginService.loginUser(loginRequest);

        response.status(200);
        //response.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }


    //Logs out the user represented by the authToken.
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

    //list game
    private Object getGames(Request request, Response response) throws DataAccessException {
        try {
            response.type("application/json");
            var authToken = request.headers("authorization");
            gameService.authentication(authToken);

            List<GameDataResult> allGames = gameService.ListGame();

            response.status(200);
            //response.body(new Gson().toJson(new ListRequest(allGames)));
            return new Gson().toJson(new ListRequest(allGames));
        } catch (DataAccessException e) {
            exceptionHandler(e, request, response);
            FailureResponse failureResponse = new FailureResponse(e.getMessage());
            return new Gson().toJson(failureResponse);
        }

        // return "";
    }


    //Create game
    private Object createGame(Request request, Response response) throws DataAccessException {
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


    // join game
    private Object joinGame(Request request, Response response) throws DataAccessException {
        var authToken = request.headers("authorization");
        gameService.authentication(authToken);

        AuthData authData = gameService.getAuthData(authToken);

        var joinData = new Gson().fromJson(request.body(), JoinRequest.class);
        gameService.JoinGame(joinData, authData);

        response.status(200);
        return "{}";
    }


    //clear game data
    private Object clearData(Request request, Response response) throws DataAccessException {
        clearService.clearDatabase();
        response.status(200);
        return "{}";
    }

}
