package ui;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import result.LoginRequest;
import server.ServerFacade;


import java.util.Arrays;

public class PreLogin {
    private final String serverURL;
    private int state = 0;

    private String auth = null;
    private final ServerFacade serverFacade;

    public PreLogin(String serverURL) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> signIn(params);
                case "quit" -> "quit";
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - Help
                - Quit
                - Login
                - Register
                """;
    }

    public String signIn(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest loginRequest = new LoginRequest(username, password);
            AuthData res = serverFacade.loginUser(loginRequest);
            this.auth = res.authToken();
            state = 1;
            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

//    public String getAuth() {
//        return auth;
//    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            AuthData registration = serverFacade.registerUser(user);
            this.auth = registration.authToken();
            state = 1;
            return String.format("Registered user %s", username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

}
