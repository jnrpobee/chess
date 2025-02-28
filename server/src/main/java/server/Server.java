package server;

import dataaccess.service.ClearService;
import dataaccess.service.JoinGameService;
import dataaccess.service.ListGameService;
import dataaccess.service.LoginService;
import dataaccess.service.LogoutService;
import dataaccess.service.RegisterService;
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

    
}
