package chess.moves;

import chess.*;

import java.util.Collection;

public class Pawn implements MovesPiece {

    private boolean cover(ChessPosition myPosition) {
        return myPosition.getRow() >= 1 && myPosition.getRow() <= 8 && myPosition.getColumn() >= 1 && myPosition.getColumn() <= 8;
    }

    @Override
    public void getMoves(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
        ChessPosition newPosition; // New position for the pawn
        ChessPiece.PieceType[] promotionTypes = {
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP
        }; // Promotion types

        //int direction = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? -1 : 1; // Direction of the pawn
        int direction; //Direction of the pawn
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        } else {
            direction = 1;
        }
        int startRow = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 7 : 2; // Starting row of the pawn
        int promotionRow = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 2 : 7; // Promotion row of the pawn
        boolean canPromote = row == promotionRow;

        // Move forward
        newPosition = new ChessPosition(row + direction, col);
        // Check if the position is empty and the move is valid 
        if (board.getPiece(newPosition) == null) {
            if (canPromote) {
                for (ChessPiece.PieceType type : promotionTypes) {
                    validMoves.add(new ChessMove(myPosition, newPosition, type));
                }
            } else {
                // Add the move to the valid moves list if the position is empty and the move is valid 
                validMoves.add(new ChessMove(myPosition, newPosition, null));
                if (row == startRow) {
                    newPosition = new ChessPosition(row + 2 * direction, col);
                    if (board.getPiece(newPosition) == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        // Capture diagonally
        int[] diagonalDirections = {1, -1}; // Diagonal directions
        // Loop through the diagonal directions
        for (int diagonalDirection : diagonalDirections) {
            newPosition = new ChessPosition(row + direction, col + diagonalDirection);
            // Check if the position is empty and the move is valid
            if (cover(newPosition)) {
                ChessPiece targetPiece = board.getPiece(newPosition);
                handleCaptureMove(piece, myPosition, validMoves, targetPiece, canPromote, promotionTypes, newPosition);
            }
        }
    }

    private static void handleCaptureMove(ChessPiece piece, ChessPosition myPosition, Collection<ChessMove> validMoves, ChessPiece targetPiece, boolean canPromote, ChessPiece.PieceType[] promotionTypes, ChessPosition newPosition) {
        if (targetPiece != null && targetPiece.getTeamColor() != piece.
                // Add the move to the valid moves list if the position is not empty and the move is valid
                        getTeamColor()) {
            if (canPromote) {
                for (ChessPiece.PieceType type : promotionTypes) {
                    validMoves.add(new ChessMove(myPosition, newPosition, type));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }
}
