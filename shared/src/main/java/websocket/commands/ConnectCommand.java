package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    private final ChessGame.TeamColor playerColor;

    public ConnectCommand(String authData, int gameID, ChessGame.TeamColor playerColor) {
        super(CommandType.CONNECT, authData, gameID);
        // if (playerColor == null) {
        //     throw new IllegalArgumentException("Player color cannot be null");
        // }
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
