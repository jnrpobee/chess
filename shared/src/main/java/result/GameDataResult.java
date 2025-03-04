package result;

import chess.ChessGame;

public record GameDataResult(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
}
