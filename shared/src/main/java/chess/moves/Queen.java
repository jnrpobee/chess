package chess.moves;

import chess.*;

import java.util.Collection;

public class Queen implements MovesPiece {
    @Override
    public void getMoves(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
        int[][] directions = {
                {-1, -1}, // top-left
                {-1, 1},  // top-right
                {1, -1},  // bottom-left
                {1, 1},   // bottom-right
                {-1, 0},  // up
                {1, 0},   // down
                {0, -1},  // left
                {0, 1}    // right
        };

        QueenRookNBishopDirections.addMoves(piece, board, myPosition, row, col, directions, validMoves);
    }
}
