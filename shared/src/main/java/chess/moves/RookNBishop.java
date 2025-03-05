package chess.moves;

import chess.*;
//import chess.ChessBoard;
//import chess.ChessMove;
//import chess.ChessPiece;
//import chess.ChessPosition;

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

        int[][] directions = piece.getPieceType() == ChessPiece.PieceType.BISHOP ? bishopDirections : rookDirections;

        QueenRookNBishopDirections.addMoves(piece, board, myPosition, row, col, directions, validMoves);
    }
}
