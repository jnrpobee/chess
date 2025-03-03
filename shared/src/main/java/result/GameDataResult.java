package result;

import chess.ChessGame;

public record GameDataResult(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
    public boolean isInUse(ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE && whiteUsername() != null);
    }
}
