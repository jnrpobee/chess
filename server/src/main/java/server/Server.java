package server;

import com.google.gson.Gson;
import dataaccess.handler.LoginRequest;
import dataaccess.handler.LogoutRequest;
import dataaccess.handler.RegisterRequest;
import dataaccess.service.ClearService;
import dataaccess.service.JoinGameService;
import dataaccess.service.ListGameService;
import dataaccess.service.LoginService;
import dataaccess.service.LogoutService;
import dataaccess.service.RegisterService;
import model.AuthData;
import spark.*;
import dataaccess.*;


public class Server {
    private RegisterService registerService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListGameService listGameService;
    private JoinGameService joinGameService;
    private ClearService clearService;


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        // Register handlers for each endpoint using the method reference syntax
        Spark.post("/user", this::registration);

        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);


        Spark.awaitInitialization();


        return Spark.port();
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
        authService.authentication(authToken.authToken());
        //AuthData authData = logoutService.logoutUser(logoutRequest);

        logoutService.logoutUser(authToken);
        res.status(200);
        return "{}";
    }

}
