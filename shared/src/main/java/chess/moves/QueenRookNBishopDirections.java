package chess.moves;

import chess.*;

import java.util.Collection;

public class QueenRookNBishopDirections {
    public static void addMoves(
            ChessPiece piece, ChessBoard board,
            ChessPosition myPosition, int row,
            int col, int[][] directions,
            Collection<ChessMove> validMoves) {

        for (int[] direction : directions) {
            int currentRow = row;
            int currentCol = col;

            while (true) {
                currentRow += direction[0];
                currentCol += direction[1];

                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                    break; // Out of bounds
                }

                ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
                ChessPiece targetPiece = board.getPiece(newPosition);

                if (targetPiece == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null)); // Empty square
                } else {
                    if (targetPiece.getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null)); // Capture opponent's piece
                    }
                    break; // Stop at the first piece encountered
                }
            }
        }
    }
}
