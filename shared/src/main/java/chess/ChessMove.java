package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;
    private static final String[] COLS = {"a", "b", "c", "d", "e", "f", "g", "h"};

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {

        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {

        return promotionPiece;
    }

    private String parsePosition (ChessPosition pos){
        return COLS [pos.getColumn()-1] + pos.getRow();
    }

    @Override
    public String toString() {
        return parsePosition(getStartPosition()) + " " + parsePosition(getEndPosition());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessMove other) {
            return this.startPosition.equals(other.startPosition) &&
                    this.endPosition.equals(other.endPosition) &&
                    this.promotionPiece == other.promotionPiece;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }


}
