package chess;

import java.util.ArrayList;
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

    public static final String WHITE_PAWN = "P";
    public static final String WHITE_ROOK = "R";
    public static final String WHITE_kNIGHT = "KT";
    public static final String WHITE_BISHOP = "B";
    public static final String WHITE_QUEEN = "Q";
    public static final String WHITE_KING = "k";
    public static final String BLACK_PAWN = "P";
    public static final String BLACK_ROOK = "R";
    public static final String BLACK_KNIGHT = "KT";
    public static final String BLACK_BISHOP = "B";
    public static final String BLACK_QUEEN = "B";
    public static final String BLACK_KING = "K";




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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> ValidMoves = new HashSet<>();

        ChessPiece piece = board.getPiece(myPosition);

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //Pawn
        if(piece.getPieceType() == PieceType.PAWN){
            pawn(piece, myPosition,board, row, col, ValidMoves);
        }

        //ROOK and Queen
        if(piece.getPieceType() == PieceType.ROOK || piece.getPieceType() == PieceType.QUEEN){
            rook_n_queen(piece, board, myPosition, row, col, ValidMoves);
        }

        //knight
        if(piece.getPieceType() == PieceType.KNIGHT){
            knight(piece, board, myPosition, row, col, ValidMoves);
        }

        //bishop and queen
        if(piece.getPieceType() == PieceType.BISHOP|| piece.getPieceType() == PieceType.QUEEN){
            bishop_n_queen(piece, board, myPosition, row, col, ValidMoves);
        }
        
        //king
        if(piece.getPieceType() == PieceType.KING){
            king(piece, board, myPosition, row, col, ValidMoves);
        }

        return ValidMoves;

    }



    private void king(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
    }

    private void knight(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
    }

    private void bishop_n_queen(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
    }

    private void rook_n_queen(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
    }

    private void pawn(ChessPiece piece, ChessPosition myPosition, ChessBoard board, int row, int col, Collection<ChessMove> validMoves) {
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
