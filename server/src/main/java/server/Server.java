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
        Spark.init();

        //endpoint
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

    private Object registration(Request req, Response res) {
        res.type("application/json");
        var user = new Gson().fromJson(req.body(), RegisterRequest.class)
        AuthData authData = registerService.registerUser(user);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    /**
     * Logs in new users
     * @param request HTTP request - body is probed for username and password
     * @param response HTTP response
     * @return JSON of the authorization data upon successful login
     * @throws ResponseException if unsuccessful log in, indicating incorrect password or other errors
     */
    private Object loginUser(Request req, Response res) {
        res.type("application/json");
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        AuthData authData = loginService.loginUser(loginRequest);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    /**
     * Logs out users
     * @param request HTTP request - body is probed for username and password
     * @param response HTTP response
     * @return JSON of the authorization data upon successful logout
     * @throws ResponseException if unsuccessful log out, indicating incorrect password or other errors
     */
    private Object logoutUser(Request req, Response res) {
        res.type("application/json");
        var logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        AuthData authData = logoutService.logoutUser(logoutRequest);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

}
