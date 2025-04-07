package ui;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class GamePlay {
    private final String serverURL;

    private final ChessGame chessGame = new ChessGame();
    private WebSocketFacade ws;

    public int gameID;
    public int state = 2;
    public String authData;
    private final ServerFacade serverFacade;

    private final NotificationHandler notificationHandler;

    public enum Perspective {
        WHITE, BLACK, OBSERVER
    }

    private Perspective playerPerspective;

    public GamePlay(String serverURL, String authData, String playerColor, NotificationHandler notificationHandler) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
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

    public void setPlayerPerspective(Perspective perspective) {
        this.playerPerspective = perspective;
        System.out.println("Player perspective set to: " + perspective);
        System.out.println(drawBoard(chessGame)); // Display the board based on the player's perspective
    }


    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
//            case "exit" -> {
//                exitGame();
//                yield "Exited Gameplay";
//            }
            case "exit" -> exitGame();
            case "help" -> help();
            case "draw" -> drawBoard(chessGame);
            case "highlight" -> highlightMoves(chessGame, params);
            case "leave" -> leaveGame();
            case "resign" -> resignGame();
            case "move" -> makeMove(params);
            //case "Quit" -> "quit";
            default -> "";
        };
    }

    public String help() {
        System.out.println("\n " + SET_TEXT_BOLD + "Gameplay Help Menu");
        return """
                - Exit
                - Help
                - Draw
                - Move
                - Highlight
                - Leave
                - Resign
                """;
    }


    public String exitGame() {
        this.state = 1; // Set state to 1 to return to postLogin
        System.out.println("Returning to postLogin.");
        return "Exited Gameplay";
    }

    public String highlightMoves(ChessGame game, String... params) {
        if (params.length != 1 || !isValidPosition(params[0])) {
            return "Usage: highlight <position>";
        }
        System.out.println(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
        String[][] board = new String[8][8];
        ChessBoard board2 = game.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board2.getPiece(new ChessPosition(i + 1, j + 1));
                board[i][j] = (piece != null) ? getPieceSymbol(piece) : EMPTY;
            }
        }
        ChessPosition piecePosition = convertPosition(params[0]);
        Collection<ChessMove> moves = game.validMoves(piecePosition);
        for (ChessMove move : moves) {
            int row = move.getEndPosition().getRow();
            int col = move.getEndPosition().getColumn();
            board[row - 1][col - 1] = "x";
        }
        StringBuilder result = new StringBuilder();
        if (playerPerspective == Perspective.BLACK) {
            result.append("  h\u2003g\u2003f\u2003e\u2003d\u2003c\u2003b\u2003a\n");
            result.append(" +--------------------+\n");
            for (int i = 0; i < 8; i++) {
                result.append(i + 1).append("|");
                for (int j = 7; j >= 0; j--) {
                    result.append(board[i][j]).append("|");
                }
                result.append("\n");
            }
        } else {
            result.append("  a\u2003b\u2003c\u2003d\u2003e\u2003f\u2003g\u2003h\n");
            result.append(" +--------------------+\n");
            for (int i = 7; i >= 0; i--) {
                result.append(i + 1).append("|");
                for (int j = 0; j < 8; j++) {
                    result.append(board[i][j]).append("|");
                }
                result.append("\n");
            }
        }
        result.append(" +--------------------+\n");
        return result.toString();
    }

    public String drawBoard(ChessGame game) {
        StringBuilder result = new StringBuilder();
        ChessBoard board = game.getBoard();

        if (playerPerspective == Perspective.BLACK) {
            result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY)
                    .append("   h  g   f   e   d  c   b  a").append(RESET_BG_COLOR).append("\n");
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
            result.append(SET_TEXT_COLOR_WHITE).append(SET_BG_COLOR_DARK_GREY)
                    .append("   a  b   c   d   e   f  g   h").append(RESET_BG_COLOR).append("\n");
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
            result.append("   a  b   c   d   e   f  g   h\n").append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        }
        return result.toString();
    }

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

    public String makeMove(String... params) throws ResponseException {
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
            ws.makeMove(new AuthData(authData, endPos), gameID, move);
            return "";
        } else {
            return "Format: move <start-position> <end-position>";
        }
    }

    public String leaveGame() throws ResponseException {
        this.ws = new WebSocketFacade(serverURL, notificationHandler);
        ws.leaveGame(new AuthData(authData, authData), gameID);
        this.state = 1;
        return "Left the game";
    }

    public String resignGame() throws ResponseException {
        this.ws = new WebSocketFacade(serverURL, notificationHandler);
        ws.resignGame(new AuthData(authData, authData), gameID);
        return "Resigned from game";
    }

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

}