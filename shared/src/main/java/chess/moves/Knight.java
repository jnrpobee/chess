package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class Knight implements MovesPiece {
    @Override
    public void getMoves(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, // Up-left, Up-right
                {-1, -2}, {-1, 2}, // Left-up, Right-up
                {1, -2}, {1, 2},   // Left-down, Right-down
                {2, -1}, {2, 1}    // Down-left, Down-right
        };

        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);

                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }
}
