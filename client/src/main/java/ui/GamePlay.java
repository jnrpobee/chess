package ui;

import chess.*;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlay {
    private final String serverURL;

    private ChessGame chessGame;

    private int gameID;
    public int state = 2;
    private String authData;
    private final ServerFacade serverFacade;


    public GamePlay(String serverURL, String authData) {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
    }

    public String eval(String input) throws ResponseException {
        //try {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "quit" -> "quit";
            case "help" -> help();
            case "draw" -> theGameBoard(chessGame);
            default -> "";
        };
//        } catch (ResponseException e) {
//            return e.getMessage();
//        }
    }

    public String help() {
        return """
                - Help
                - Quit
                - Draw
                - Move
                - Highlight
                - Leave
                - Resign
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

//    public String makeMove(String... params) throws ResponseException {
//        if (params.length == 2) {
//            String startPos = params[0].toLowerCase();
//            String endPos = params[1].toLowerCase();
//            if (!isValidPosition(startPos) || !isValidPosition(endPos)) {
//                return "provide a valid position letter a-h number 1-8:";
//            }
//            ChessPosition start = convertPosition(startPos);
//            ChessPosition end = convertPosition(endPos);
//            ChessMove move = new ChessMove(start, end, null);
//            return "";
//        } else {
//            return "Move <start-position> <end-position>";
//        }
//    }

    private ChessPosition convertPosition(String positionString) {
        int row = 0;
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


//    private boolean isValidPosition(String positionString) {
//        if (positionString.length() != 2) {
//            return false;
//        }
//        Set<Character> charSet = new HashSet<>();
//        // Add characters 'a' to 'h'
//        for (char c = 'a'; c <= 'h'; c++) {
//            charSet.add(c);
//        }
//        // Add characters '1' to '8'
//        for (char c = '1'; c <= '8'; c++) {
//            charSet.add(c);
//        }
//        return charSet.contains(positionString.charAt(0)) && charSet.contains(positionString.charAt(1));
//    }

}
