package chess.Moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public interface MovesPiece {
    void getMoves (ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves);
}
