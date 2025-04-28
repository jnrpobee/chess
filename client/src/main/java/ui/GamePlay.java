package ui;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import static ui.EscapeSequences.*;

import java.util.*;


public class GamePlay {
    private final String serverURL;

    private ChessGame chessGame = new ChessGame();
    private WebSocketFacade ws;

    public int gameID;
    public int state = 2;
    private String authData;
    private final ServerFacade serverFacade;

    private final NotificationHandler notificationHandler;

    public enum Perspective {
        WHITE, BLACK, OBSERVER
    }

    private Perspective playerPerspective;

    public GamePlay(String serverURL, String authData, String playerColor, NotificationHandler notificationHandler) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        //ServerFacade serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
        this.notificationHandler = notificationHandler;
        if ("black".equalsIgnoreCase(playerColor)) {
            this.playerPerspective = Perspective.BLACK;
        } else if ("white".equalsIgnoreCase(playerColor)) {
            this.playerPerspective = Perspective.WHITE;
        } else {
            this.playerPerspective = Perspective.OBSERVER;
        }
    }

    public GamePlay(String serverURL, String authData, NotificationHandler notificationHandler) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
        this.notificationHandler = notificationHandler;
    }

    /**
     * Sets the player's perspective (WHITE, BLACK, or OBSERVER) and prints the updated perspective.
     */
    public void setPlayerPerspective(Perspective perspective) {
        this.playerPerspective = perspective;
        System.out.println("Player perspective set to: " + perspective);
        //System.out.println(drawBoard(chessGame)); // Display the board based on the player's perspective
    }


    /**
     * Evaluates the input command and executes the corresponding action.
     * Supported commands: exit, help, redraw, move, highlight, leave, resign.
     */
    public String eval(String input) throws ResponseException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "exit" -> exitGame();
                case "help" -> help();
                case "redraw" -> redrawBoard();
                case "move" -> makeMove();
                case "highlight" -> highlightMoves(chessGame);
                case "leave" -> leaveGame();
                case "resign" -> resignGame();
                default -> "";
            };
        } catch (ResponseException ex) {
            return "Failed! try again" + ex.getMessage();
        }
    }

    /**
     * Displays the help menu with a list of available commands.
     */
    public String help() {
        System.out.println("\n " + SET_TEXT_BOLD + "Gameplay Help Menu");
        return """
                - Exit
                - Help
                - Redraw
                - Move
                - Highlight
                - Leave
                - Resign
                """;
    }


    /**
     * Exits the game and sets the state to return to the post-login screen.
     */
    public String exitGame() {
        this.state = 1; // Set state to 1 to return to postLogin
        System.out.println("Returning to postLogin.");
        return "Exited Gameplay";
    }

    /**
     * Highlights valid moves for a piece at a given position on the chessboard.
     * Returns a visual representation of the board with highlighted moves.
     */
    public String highlightMoves(ChessGame game) {
        System.out.println("highlight <position>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);
        if (params.length != 1 || !isValidPosition(params[0])) {
            return "Usage: highlight <position>";
        }
        StringBuilder result = new StringBuilder();
        ChessBoard board = game.getBoard();

        ChessPosition piecePosition = convertPosition(params[0]);
        Collection<ChessMove> moves = game.validMoves(piecePosition);

        if (playerPerspective == Perspective.BLACK) {
            result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append("\n")
                    .append("  h  g   f   e   d  c   b  a").append(RESET_BG_COLOR).append("\n");
            for (int row = 1; row <= 8; row++) {
                int currentRow = row;
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(row);
                for (int col = 8; col >= 1; col--) {
                    int currentCol = col;
                    ChessPiece piece = board.getPiece(new ChessPosition(row, currentCol));
                    String squareColor = (row + currentCol) % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_BLUE;
                    String pieceSymbol = (piece != null) ? getPieceSymbol(piece) : EMPTY;

                    // Highlight valid moves in green
                    boolean isHighlighted = moves.stream()
                            .anyMatch(move -> move.getEndPosition().getRow() == currentRow &&
                                    move.getEndPosition().getColumn() == currentCol);
                    // Check if there are no valid moves to highlight
                    if (moves.isEmpty()) {
                        return "no valid move";
                    }
                    if (isHighlighted) {
                        squareColor = SET_BG_COLOR_GREEN;
                    }


                    result.append(squareColor).append(pieceSymbol).append(RESET_BG_COLOR);
                }
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(" ").append(row)
                        .append(RESET_BG_COLOR).append("\n");
            }
            result.append("  h   g  f   e   d   c   b  a\n").append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        } else {
            result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append("\n")
                    .append("\n  a   b   c   d   e   f  g   h").append(RESET_BG_COLOR).append("\n");
            for (int row = 8; row >= 1; row--) {
                final int currentRow = row;
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(currentRow);
                for (int col = 1; col <= 8; col++) {
                    final int currentCol = col;
                    ChessPiece piece = board.getPiece(new ChessPosition(currentRow, currentCol));
                    String squareColor = (currentRow + currentCol) % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_LIGHT_GREY;
                    String pieceSymbol = (piece != null) ? getPieceSymbol(piece) : EMPTY;

                    // Highlight valid moves in green
                    boolean isHighlighted = moves.stream()
                            .anyMatch(move -> move.getEndPosition().getRow() == currentRow &&
                                    move.getEndPosition().getColumn() == currentCol);
                    if (moves.isEmpty()) {
                        return "no valid moves";
                    }
                    if (isHighlighted) {
                        squareColor = SET_BG_COLOR_GREEN;
                    }

                    result.append(squareColor).append(pieceSymbol).append(RESET_BG_COLOR);
                }
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(" ")
                        .append(currentRow).append(RESET_BG_COLOR).append("\n");
            }
            result.append("  a   b   c   d   e   f  g   h\n").append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        }
        return result.toString();
    }

    /**
     * Redraws the chessboard based on the current game state.
     * Throws an exception if the game is not in progress.
     */
    private String redrawBoard() throws ResponseException {
        System.out.println(state);
        if (state != 2) { // Assuming state 2 represents "in game"
            throw new ResponseException(400, "Only available in game");
        }
        if (ws == null) {
            this.ws = new WebSocketFacade(serverURL, notificationHandler);
        }
        ws.connect(authData, gameID);
        //System.out.println(drawBoard(chessGame)); // Redraw the board
        return "Board redrawn";
    }


    /**
     * Draws the chessboard from the perspective of the current player.
     * Supports both WHITE and BLACK perspectives.
     */
    public String drawBoard(ChessGame game) {
        this.chessGame = game;
        StringBuilder result = new StringBuilder();
        ChessBoard board = game.getBoard();

        if (playerPerspective == Perspective.BLACK) {
            result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append("\n")
                    .append("  h  g   f   e   d  c   b  a").append(RESET_BG_COLOR).append("\n");
            for (int row = 1; row <= 8; row++) {
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(row);
                for (int col = 8; col >= 1; col--) {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    String squareColor = (row + col) % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_BLUE;
                    String pieceSymbol = (piece != null) ? getPieceSymbol(piece) : EMPTY;
                    result.append(squareColor).append(pieceSymbol).append(RESET_BG_COLOR);
                }
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(" ").append(row)
                        .append(RESET_BG_COLOR).append("\n");
            }
            result.append("  h   g  f   e   d   c   b  a\n").append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        } else {
            result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append("\n")
                    .append("\n  a   b   c   d   e   f  g   h").append(RESET_BG_COLOR).append("\n");
            for (int row = 8; row >= 1; row--) {
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(row);
                for (int col = 1; col <= 8; col++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    String squareColor = (row + col) % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_LIGHT_GREY;
                    String pieceSymbol = (piece != null) ? getPieceSymbol(piece) : EMPTY;
                    result.append(squareColor).append(pieceSymbol).append(RESET_BG_COLOR);
                }
                result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY).append(" ")
                        .append(row).append(RESET_BG_COLOR).append("\n");
            }
            result.append("  a   b   c   d   e   f  g   h\n").append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        }
        return result.toString();
    }

    /**
     * Returns the symbol representation of a chess piece based on its type and team color.
     */
    private String getPieceSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case ChessPiece.PieceType.PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            case ChessPiece.PieceType.KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case ChessPiece.PieceType.QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case ChessPiece.PieceType.BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case ChessPiece.PieceType.KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ChessPiece.PieceType.ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            default:
                return EMPTY;
        }
    }

    /**
     * Processes a move command by validating the input, sending the move to the server,
     * and updating the local game state. Displays the updated board after the move.
     */
    public String makeMove() throws ResponseException {
        System.out.println("move <start-position> <end-position>");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 0, tokens.length);
        if (params.length == 2) {
            this.ws = new WebSocketFacade(serverURL, notificationHandler);
            String startPos = params[0].toLowerCase();
            String endPos = params[1].toLowerCase();
            if (!isValidPosition(startPos) || !isValidPosition(endPos)) {
                return "Enter valid position letter a-h number 1-8:";
            }
            ChessPosition start = convertPosition(startPos);
            ChessPosition end = convertPosition(endPos);
            ChessMove move = new ChessMove(start, end, null);

//            // A player shouldn't be able to move a piece of the opponent
            ChessPiece pieceAtStart = chessGame.getBoard().getPiece(start);

            // Send the move to the server
            ws.makeMove(new AuthData(authData, endPos), gameID, move);


            if ((playerPerspective == Perspective.WHITE && pieceAtStart.getTeamColor() != ChessGame.TeamColor.WHITE) ||
                    (playerPerspective == Perspective.BLACK && pieceAtStart.getTeamColor() != ChessGame.TeamColor.BLACK)) {
                return "You cannot move your opponent's piece!";
            }

            // Display the updated board for all perspectives
            System.out.println(drawBoard(chessGame));
            if (playerPerspective == Perspective.OBSERVER) {
                System.out.println("Observer view updated.");
            }
            return "Move made: " + startPos + " to " + endPos;
        } else {
            return "Format: move <start-position> <end-position>";
        }
    }

    /**
     * Leaves the current game by notifying the server and updating the game state.
     */
    public String leaveGame() throws ResponseException {
        this.ws = new WebSocketFacade(serverURL, notificationHandler);
        String authToken = getAuthData(); // Dynamically retrieve the auth token
        int currentGameID = getGameID(); // Dynamically retrieve the current game ID
        ws.leaveGame(new AuthData(authToken, authToken), currentGameID);
        this.state = 1;
        return "Left the game";
    }

    /**
     * Handles the resignation process by confirming the player's intent and notifying the server.
     */
    public String resignGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you sure you want to resign? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();

        if ("yes".equals(response)) {
            this.ws = new WebSocketFacade(serverURL, notificationHandler);
            ws.resignGame(new AuthData(authData, authData), gameID);
            return "Resigned from game";
        } else if ("no".equals(response)) {
            return "Resignation canceled";
        } else {
            return "Invalid response. Resignation not processed.";
        }
    }

    /**
     * Converts a chessboard position string (e.g., "a1") into a ChessPosition object.
     */
    private ChessPosition convertPosition(String positionString) {
        int row;
        int col = 0;
        switch (positionString.charAt(0)) {
            case 'a': {
                col = 1;
                break;
            }
            case 'b': {
                col = 2;
                break;
            }
            case 'c': {
                col = 3;
                break;
            }
            case 'd': {
                col = 4;
                break;
            }
            case 'e': {
                col = 5;
                break;
            }
            case 'f': {
                col = 6;
                break;
            }
            case 'g': {
                col = 7;
                break;
            }
            case 'h': {
                col = 8;
                break;
            }
        }
        row = Character.getNumericValue(positionString.charAt(1));
        return new ChessPosition(row, col);
    }

    /**
     * Validates if a given position string (e.g., "a1") is a valid chessboard position.
     */
    private boolean isValidPosition(String positionString) {
        if (positionString.length() != 2) {
            return false;
        }
        Set<Character> charSet = new HashSet<>();

        // Add characters 'a' to 'h'
        for (char c = 'a'; c <= 'h'; c++) {
            charSet.add(c);
        }

        // Add characters '1' to '8'
        for (char c = '1'; c <= '8'; c++) {
            charSet.add(c);
        }
        return charSet.contains(positionString.charAt(0)) && charSet.contains(positionString.charAt(1));
    }

    /**
     * Retrieves the current game ID.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Sets the current game ID.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Retrieves the current game state.
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the current game state.
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Retrieves the authentication data for the current session.
     */
    public String getAuthData() {
        return authData;
    }

    /**
     * Sets the authentication data for the current session.
     */
    public void setAuthData(String authData) {
        this.authData = authData;
    }

    /**
     * Retrieves the player's current perspective (WHITE, BLACK, or OBSERVER).
     */
    public Perspective getPlayerPerspective() {
        return playerPerspective;
    }

}