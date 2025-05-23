package ui;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import result.LoginRequest;
import server.ServerFacade;

import static ui.EscapeSequences.*;


import java.util.Arrays;
import java.util.Scanner;

public class PreLogin {
    private final String serverURL;
    int state = 0;
    private String username;

    public String auth = null;
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
                case "login" -> signIn();
                case "quit" -> "quit";
                case "register" -> register();
                default -> help();
            };
        } catch (ResponseException ex) {
            return "Failed! try again";
        }
    }

    public String help() {
        System.out.println("\n " + SET_TEXT_BOLD + "PreLogin Help Menu");
        return """
                - Help
                - Quit
                - Login
                - Register
                """;
    }

    public String signIn() throws ResponseException {
        System.out.println("Enter <username> <password>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);

        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest loginRequest = new LoginRequest(username, password);
            AuthData res = serverFacade.loginUser(loginRequest);
            this.auth = res.authToken();
            state = 1;
            setUsername(username); //added this line
            return String.format("You signed in as %s.", username);

        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String getAuth() {
        return auth;
    }

    public String register() throws ResponseException {
        System.out.println("Enter <username> <password> <email>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);

        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            AuthData registration = serverFacade.registerUser(user);
            this.auth = registration.authToken();
            state = 1;
            setUsername(username); //added this line
            return String.format("Registered user %s", username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    //added sections below
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
