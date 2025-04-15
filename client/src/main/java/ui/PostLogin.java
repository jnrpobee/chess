package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.JoinGameRequest;
import result.GameDataResult;
import result.GameName;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public class PostLogin {

    private final String serverURL;
    private GamePlay gameplay;

    public int state = 1;
    String authData;
    private int gameData;
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;

    private final ServerFacade serverFacade;

    int gameID;

    private final Map<Integer, Integer> gameNumberToID = new HashMap<>();

    public PostLogin(String serverURL, String authData, NotificationHandler notificationHandler, GamePlay gamePlay) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
        this.notificationHandler = notificationHandler;
        this.gameplay = gamePlay;
        this.gameID = 0;
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
                case "join" -> updateGame();
                case "observe" -> observe();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return "Failed! try again";
        }
    }


    public String help() {
        System.out.println("\n " + SET_TEXT_BOLD + "PostLogin Help Menu");
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
            state = 0;
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
            //return String.format("Created Game: %s (id: %s)", gameName, gameID);
            return String.format("Created Game: %s", gameName);
        }
        throw new ResponseException(400, "Expected: create <game_name>");
    }


    public String updateGame() throws ResponseException {
        System.out.println("join <black or white> <game_number>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);
        if (params.length == 2) {
            this.ws = new WebSocketFacade(serverURL, notificationHandler);
            String playerColor = params[0];
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid game number format. Expected: join <black or white> <game_number>");
            }

            if (!gameNumberToID.containsKey(gameNumber)) {
                throw new ResponseException(404, "Game not found with number: " + gameNumber);
            }

            int gameID = gameNumberToID.get(gameNumber);

            if (!playerColor.equals("black") && !playerColor.equals("white")) {
                throw new ResponseException(400, "Invalid color. Expected: black or white");
            }

            JoinGameRequest joinTheGame = new JoinGameRequest(gameID, playerColor);
            serverFacade.joinGame(joinTheGame, authData);

            // Pass the game ID to the WebSocket connection
            // and set the player color
            ws.connect(authData, gameID);

            this.state = 2;
            this.gameID = gameID;

            // Pass the player's color to GamePlay
            gameplay.setAuthData(authData);
            gameplay.setPlayerPerspective(playerColor.equals("black") ? GamePlay.Perspective.BLACK : GamePlay.Perspective.WHITE);

            gameplay.setGameID(gameID);

            return String.format("Joined Game: %d as %s", gameNumber, playerColor);
        }
        throw new ResponseException(400, "Expected: join <black or white> <game_number>");
    }


    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            StringBuilder result = new StringBuilder("GAMES LIST:\n");
            Collection<GameDataResult> gamesList = serverFacade.listGames(authData);
            gameNumberToID.clear(); // Clear previous mappings
            int gameNumber = 1;
            for (GameDataResult game : gamesList) {
                gameNumberToID.put(gameNumber, game.gameID());
                result.append(String.format("%d. Game Name: %s\n", gameNumber, game.gameName()));
                result.append("   White: ").append(game.whiteUsername()).append("\n");
                result.append("   Black: ").append(game.blackUsername()).append("\n\n");
                gameNumber++;
            }
            return result.toString();
        }
        throw new ResponseException(400, "Expected: list");
    }

    public String observe() throws ResponseException {
        System.out.println("observe <game_number>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        if (tokens.length == 1) {
            try {
                int gameNumber = Integer.parseInt(tokens[0]);
                if (!gameNumberToID.containsKey(gameNumber)) {
                    throw new ResponseException(404, "Game not found with number: " + gameNumber);
                }
                int gameID = gameNumberToID.get(gameNumber);
                Collection<GameDataResult> gamesList = serverFacade.listGames(authData);
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

                gameplay.setAuthData(authData);
                gameplay.setPlayerPerspective(GamePlay.Perspective.OBSERVER);
                gameplay.setGameID(gameID);

                return String.format("Observing Game: %s", gameName);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid game number format. Expected: observe <game_number>");
            }
        }
        throw new ResponseException(400, "Expected: observe <game_number>");
    }

    public String getAuth() {
        return authData;
    }
}
