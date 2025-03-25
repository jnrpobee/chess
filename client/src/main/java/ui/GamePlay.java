package ui;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlay {
    private final String serverURL;

    private ChessGame chessGame;

    private int gameID;
    private int state = 2;
    private String authData;
    private final ServerFacade serverFacade;


    public GamePlay(String serverURL, String authData) {
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
                case "quit" -> "quit";
                case "help" -> help();
                case "draw" -> theGameBoard(chessGame);
                case "move" -> makeMove(params);
                case "leave" -> leaveGame();
                default -> "";
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() {
        return """
                - Help
                - Quit
                - Draw
                - Move - make a move <start-position> <end-position>
                - Highlight - highlight moves for a piece <piece-position>
                - Leave - leave the game
                - Resign - Forfeit the game (you will be given a loss and game will be ended)
                """;
    }

    public String theGameBoard(ChessGame game) {
        System.out.println(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
        String[][] board = new String[8][8];
        ChessBoard board2 = game.getBoard();
        this.chessGame = game;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board2.getPiece(new ChessPosition(i + 1, j + 1));
                if (piece != null) {
                    switch (piece.getPieceType()) {
                        case ChessPiece.PieceType.PAWN: {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                board[i][j] = WHITE_PAWN;
                            } else {
                                board[i][j] = BLACK_PAWN;
                            }
                            break;
                        }
                        case ChessPiece.PieceType.KING: {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                board[i][j] = WHITE_KING;
                            } else {
                                board[i][j] = BLACK_KING;
                            }
                            break;
                        }
                        case ChessPiece.PieceType.BISHOP: {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                board[i][j] = WHITE_BISHOP;
                            } else {
                                board[i][j] = BLACK_BISHOP;
                            }
                            break;
                        }
                        case ChessPiece.PieceType.KNIGHT: {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                board[i][j] = WHITE_KNIGHT;
                            } else {
                                board[i][j] = BLACK_KNIGHT;
                            }
                            break;
                        }
                        case ChessPiece.PieceType.ROOK: {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                board[i][j] = WHITE_ROOK;
                            } else {
                                board[i][j] = BLACK_ROOK;
                            }
                            break;
                        }
                        case ChessPiece.PieceType.QUEEN: {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                board[i][j] = WHITE_QUEEN;
                            } else {
                                board[i][j] = BLACK_QUEEN;
                            }
                            break;
                        }
                        default: {
                            board[i][j] = EMPTY;
                            break;
                        }
                    }
                } else {
                    board[i][j] = EMPTY;
                }
            }
        }
        StringBuilder result = new StringBuilder();
        result.append("  h\u2003g\u2003f\u2003e\u2003d\u2003c\u2003b\u2003a\n");
        result.append(" +---------+\n");
        for (int i = 7; i >= 0; i--) {
            result.append(i + 1).append("|");
            for (int j = 7; j >= 0; j--) {
                result.append(board[i][j]).append("|");
            }
            result.append("\n");
        }
        result.append(" +-----------+\n");

        result.append("  a\u2003b\u2003c\u2003d\u2003e\u2003f\u2003g\u2003h\n");
        result.append(" +------------+\n");
        for (int i = 0; i < 8; i++) {
            result.append(i + 1).append("|");
            for (int j = 0; j < 8; j++) {
                result.append(board[i][j]).append("|");
            }
            result.append("\n");
        }
        result.append(" +--------+\n");

        return result.toString();
    }

    public String makeMove(String... params) throws ResponseException {
        if (params.length == 2) {
            String startPos = params[0].toLowerCase();
            String endPos = params[1].toLowerCase();
            if (!isValidPosition(startPos) || !isValidPosition(endPos)) {
                return "provide a valid position letter a-h number 1-8:";
            }
            ChessPosition start = convertPosition(startPos);
            ChessPosition end = convertPosition(endPos);
            ChessMove move = new ChessMove(start, end, null);
            serverFacade.makeMove(new AuthData(authData.authToken()), gameID, move);
            return "";
        } else {
            return "Move <start-position> <end-position>";
        }
    }

}
