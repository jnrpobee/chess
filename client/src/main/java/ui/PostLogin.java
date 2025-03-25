package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import result.CreateGameResult;
import result.GameDataResult;
import result.GameName;
import result.ListGameRequest;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class PostLogin {

    private final String serverURL;


    private int state = 1;
    private final String authData;
    private int gameData;

    private final ServerFacade serverFacade;

    int gameID = 0;

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
                case "join" -> updateGame(params);
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


    public String updateGame(String... params) throws ResponseException {
        if (params.length == 2 || params.length == 1) {
            AuthData info = new AuthData(authData.authToken());
            String playerColor = null;
            int gameID = 0;
            if (params.length == 2) {
                playerColor = params[0];
                gameID = Integer.parseInt(params[1]);

                ChessGame.TeamColor teamColor = null;
                if (Objects.equals("Black", playerColor)) {
                    teamColor = ChessGame.TeamColor.BLACK;
                } else if (Objects.equals("White", playerColor)) {
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                JoinGameRequest joinTheGame = new JoinGameRequest(gameID, playerColor);
                serverFacade.joinGame(info, joinTheGame);
            } else {
                gameID = Integer.parseInt(params[0]);
            }
            this.state = 2;
            this.gameID = gameID;
            if (playerColor == null) {
                return "Joined as observer";
            }
            return String.format("Joined as team %s", playerColor);
        }
        throw new ResponseException(400, "Expected: join <black or white> <game_id>");
    }


    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            StringBuilder result = new StringBuilder("GAMES LIST:\n");
            AuthData info = new AuthData(authData);
            ListGameRequest gameList = serverFacade.listGames(info);
            Collection<GameDataResult> gamesList = gameList.games();
            for (GameDataResult game : gamesList) {
                result.append("Game ID: ").append(game.gameID()).append("\n");
                result.append("Game Name: ").append(game.gameName()).append("\n");
                result.append("White: ").append(game.whiteUsername()).append("\n");
                result.append("Black: ").append(game.blackUsername()).append("\n");
                result.append("\n");
            }
            return result.toString();
        }
        throw new ResponseException(400, "Expected: list");
    }
}
