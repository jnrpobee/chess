package chess.Moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class King implements MovesPiece {


    @Override
    public void getMoves(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
        ChessPosition newPosition;
        int rowMove;
        int colMove;
        int[] direction = {-1, 0, 1};

        for (var up : direction) {
            for (var side : direction) {
                rowMove = row + up;
                colMove = col + side;
                newPosition = new ChessPosition(rowMove, colMove);

                if (rowMove > 8 || rowMove < 1 || colMove > 8 || colMove < 1) {
                    break;
                }

                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }
}

