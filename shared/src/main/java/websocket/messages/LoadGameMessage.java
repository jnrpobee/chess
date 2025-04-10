package websocket.messages;

import chess.ChessGame;
import model.GameData;


// public class LoadGameMessage extends ServerMessage {
//     private final GameData game;
//     private ChessGame.TeamColor color;

//     public GameData getGame() {
//         return game;
//     }


//     public LoadGameMessage(GameData game) {
//         super(ServerMessageType.LOAD_GAME);
//         this.game = game;
//     }

//     public ChessGame.TeamColor getColor() {
//         return color;
//     }

//     public void setColor(ChessGame.TeamColor color) {
//         this.color = color;
//     }

// }

public class LoadGameMessage extends ServerMessage{

    private final ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    // public void setColor(ChessGame.TeamColor color) {
    //             this.color = color;
    //         }
}

