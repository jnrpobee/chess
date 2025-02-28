package chess.Moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class RookNBishop implements MovesPiece {
    @Override
    public void getMoves(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {

        int[][] bishopDirections = {
                {-1, -1}, // top-left
                {-1, 1},  // top-right
                {1, -1},  // bottom-left
                {1, 1}    // bottom-right
        };

        int[][] rookDirections = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        //int[][] directions = piece.getPieceType() == PieceType.BISHOP ? bishopDirections : rookDirections;
        //same as below
        int[][] directions;
        if(piece.getPieceType() == ChessPiece.PieceType.BISHOP){
            directions = bishopDirections;
        }
        else{
            directions = rookDirections;
        }

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
