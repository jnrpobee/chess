package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.JoinGameRequest;
import result.GameDataResult;
import result.GameName;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public class PostLogin {

    private final String serverURL;


    public int state = 1;
    String authData;
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
                case "create" -> createGame();
                case "join" -> updateGame(params);
                case "observe" -> observe();
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
                - Observe
                - Join
                """;
    }

    public String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            serverFacade.logoutUser();
            this.state = 0;
            return "Logged out successfully";
        }
        throw new ResponseException(400, "Expected: logout");
    }


    public String createGame() throws ResponseException {
        System.out.println("create <game name>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);
        if (params.length == 1) {
            String gameName = params[0];
            GameName req = new GameName(gameName);
            GameData res = serverFacade.createGame(req);
            //GameData res = serverFacade.createGame(req);
            int gameID = res.gameID();
            return String.format("Created Game: %s (id: %s)", gameName, gameID);
        }
        throw new ResponseException(400, "Expected: create <game_name>");
    }


    public String updateGame(String... params) throws ResponseException {
        if (params.length == 2 || params.length == 1) {
            //AuthData info = new AuthData(authData, authData); // added authData as a parameter to AuthData constructor to fix compilation error     
            String playerColor = null;
            int gameID = 0;
            if (params.length == 2) {
                playerColor = params[0];
                gameID = Integer.parseInt(params[1]);

                //ChessGame.TeamColor teamColor = null;
                if (Objects.equals("Black", playerColor)) {
                    //    teamColor = ChessGame.TeamColor.BLACK;
                } else if (Objects.equals("White", playerColor)) {
                    //    teamColor = ChessGame.TeamColor.WHITE;
                }
                JoinGameRequest joinTheGame = new JoinGameRequest(gameID, playerColor);
                //serverFacade.joinGame(info, joinTheGame);
                serverFacade.joinGame(joinTheGame);
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
            Collection<GameDataResult> gamesList = serverFacade.listGames();
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

    public String observe() throws ResponseException {
        System.out.println("observe <game_id>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        if (tokens.length == 1) {
            try {
                int gameID = Integer.parseInt(tokens[0]);
                Collection<GameDataResult> gamesList = serverFacade.listGames();
                String gameName = null;
                for (GameDataResult game : gamesList) {
                    if (game.gameID() == gameID) {
                        gameName = game.gameName();
                        break;
                    }
                }
                if (gameName == null) {
                    throw new ResponseException(404, "Game not found with ID: " + gameID);
                }
                this.state = 2; // Set state to observing
                this.gameID = gameID;
                return String.format("Observing Game: %s (id: %d)", gameName, gameID);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid game ID format. Expected: observe <game_id>");
            }
        }
        throw new ResponseException(400, "Expected: observe <game_id>");
    }

}
