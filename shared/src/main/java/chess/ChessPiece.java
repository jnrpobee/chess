package chess;

import chess.Moves.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType pieceType;
    private final ChessGame.TeamColor teamColor;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return this.pieceType;
    }

//    private boolean cover(ChessPosition myPosition) {
//        return myPosition.getRow() >= 1 && myPosition.getRow() <= 8 && myPosition.getColumn() >= 1 && myPosition.getColumn() <= 8;
//    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> validMoves = new HashSet<>();

        ChessPiece piece = board.getPiece(myPosition);

        MovesPiece movesPiece = null;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //Pawn
        if(piece.getPieceType() == PieceType.PAWN){
            movesPiece = new Pawn();
            //pawn(piece, myPosition,board, row, col, ValidMoves);
        }

        //Queen
        if(piece.getPieceType() == PieceType.QUEEN){
            movesPiece = new Queen();
            //rook_n_queen(piece, board, myPosition, row, col, ValidMoves);
        }

        //knight
        if(piece.getPieceType() == PieceType.KNIGHT){
            movesPiece = new Knight();
            //knight(piece, board, myPosition, row, col, ValidMoves);
        }

        //bishop rook
        if(piece.getPieceType() == PieceType.BISHOP|| piece.getPieceType() == PieceType.ROOK){
            movesPiece = new RookNBishop();
            //bishop_n_queen(piece, board, myPosition, row, col, ValidMoves);
        }
        
        //king
        if(piece.getPieceType() == PieceType.KING){
            movesPiece = new King();
            //king(piece, board, myPosition, row, col, ValidMoves);
        }

        movesPiece.getMoves(piece, board, myPosition, row, col, validMoves);

        return validMoves;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, teamColor);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceType=" + pieceType +
                ", teamColor=" + teamColor +
                '}';
    }
}
