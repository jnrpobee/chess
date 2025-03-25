package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import result.CreateGameResult;
import result.GameName;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;

public class PostLogin {

    private final String serverURL;


    private int state = 1;
    //private String authData;
    private int gameData;
    private final ServerFacade serverFacade;

    private int gameID = 0;

    //private final ServerMessageHandler serverMessageHandler;
    public PostLogin(String serverURL, String authData) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "list" -> listGames(params);
                case "create" -> createGame(params);
                //case "join" -> updateGame(params);
                case "quit" -> "quit";
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
                - Logout
                - Create
                - List
                - Join
                """;
    }

    public String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            serverFacade.logoutUser();
            state = 0;
            return "Logged out";
        }
        throw new ResponseException(400, "Expected: logout");
    }

//    public String listGames(String... params) throws ResponseException {
//        if (params.length == 0) {
//            StringBuilder result = new StringBuilder("GAMES LIST:\n");
//            AuthData info = new AuthData(authData);
//            ListGamesResponse res = serverFacade.listGames(info);
//            Collection<GameData> gamesList = res.games();
//            for (GameData game : gamesList) {
//                result.append("Game ID: ").append(game.GameID()).append("\n");
//                result.append("Game Name: ").append(game.GameName()).append("\n");
//                result.append("White: ").append(game.getWhiteUsername()).append("\n");
//                result.append("Black: ").append(game.getBlackUsername()).append("\n");
//                result.append("\n");
//            }
//            return result.toString();
//        }
//        throw new ResponseException(400, "Expected: list");
//    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            GameData info = new GameData(gameData);
            String gameName = params[0];
            GameName req = new GameName(gameName);
            CreateGameResult res = serverFacade.createGame(req, info);
            int gameID = res.gameID();
            return String.format("Created Game: %s (id: %s)", gameName, gameID);
        }
        throw new ResponseException(400, "Expected: create <game_name>");
    }
}
