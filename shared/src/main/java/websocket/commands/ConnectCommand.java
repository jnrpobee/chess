package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    private final ChessGame.TeamColor playerColor;

    public ConnectCommand(String authData, int gameID, ChessGame.TeamColor playerColor) {
        super(CommandType.CONNECT, authData, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    // @Override
    // public String toString() {
    //     return "ConnectCommand{" +
    //             "authData='" + authData + '\'' +
    //             ", gameID=" + gameID +
    //             ", playerColor=" + playerColor +
    //             ", commandType=" + commandType +
    //             '}';
    // }
}
