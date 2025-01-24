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

//    public static final String WHITE_PAWN = "P";
//    public static final String WHITE_ROOK = "R";
//    public static final String WHITE_KNIGHT = "N";
//    public static final String WHITE_BISHOP = "B";
//    public static final String WHITE_QUEEN = "Q";
//    public static final String WHITE_KING = "k";
//    public static final String BLACK_PAWN = "P";
//    public static final String BLACK_ROOK = "R";
//    public static final String BLACK_KNIGHT = "N";
//    public static final String BLACK_BISHOP = "B";
//    public static final String BLACK_QUEEN = "B";
//    public static final String BLACK_KING = "K";
    

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

        Collection<ChessMove> ValidMoves = new HashSet<>();

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
            movesPiece = new Rook_n_Bishop();
            //bishop_n_queen(piece, board, myPosition, row, col, ValidMoves);
        }
        
        //king
        if(piece.getPieceType() == PieceType.KING){
            movesPiece = new King();
            //king(piece, board, myPosition, row, col, ValidMoves);
        }

        movesPiece.getMoves(piece, board, myPosition, row, col, ValidMoves);

        return ValidMoves;

    }

//    private void king(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
//        ChessPosition newPosition;
//        int rowMove;
//        int colMove;
//        int[] direction = {-1, 0, 1};
//
//        for (var up : direction) {
//            for (var side : direction) {
//                rowMove = row + up;
//                colMove = col + side;
//                newPosition = new ChessPosition(rowMove, colMove);
//
//                if (rowMove > 8 || rowMove < 1 || colMove > 8 || colMove < 1) {
//                    break;
//                }
//
//                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
//                    validMoves.add(new ChessMove(myPosition, newPosition, null));
//                }
//            }
//        }
//    }
//
//    private void knight(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
//        int[][] knightMoves = {
//                {-2, -1}, {-2, 1}, // Up-left, Up-right
//                {-1, -2}, {-1, 2}, // Left-up, Right-up
//                {1, -2}, {1, 2},   // Left-down, Right-down
//                {2, -1}, {2, 1}    // Down-left, Down-right
//        };
//
//        for (int[] move : knightMoves) {
//            int newRow = row + move[0];
//            int newCol = col + move[1];
//
//            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
//                ChessPosition newPosition = new ChessPosition(newRow, newCol);
//                ChessPiece targetPiece = board.getPiece(newPosition);
//
//                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
//                    validMoves.add(new ChessMove(myPosition, newPosition, null));
//                }
//            }
//        }
//    }
//
//    private void bishop_n_queen(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
//        // Directions for bishop: top-left, top-right, bottom-left, bottom-right
//        int[][] directions = {
//                {-1, -1}, // top-left
//                {-1, 1},  // top-right
//                {1, -1},  // bottom-left
//                {1, 1}    // bottom-right
//        };
//
//        for (int[] direction : directions) {
//            int currentRow = row;
//            int currentCol = col;
//
//            while (true) {
//                currentRow += direction[0];
//                currentCol += direction[1];
//
//                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
//                    break; // Out of bounds
//                }
//
//                ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
//                ChessPiece targetPiece = board.getPiece(newPosition);
//
//                if (targetPiece == null) {
//                    validMoves.add(new ChessMove(myPosition, newPosition, null)); // Empty square
//                } else {
//                    if (targetPiece.getTeamColor() != piece.getTeamColor()) {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null)); // Capture opponent's piece
//                    }
//                    break; // Stop at the first piece encountered
//                }
//            }
//        }
//    }
//
//    private void rook_n_queen(ChessPiece piece, ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves) {
//        // Directions for rook: up, down, left, right
//        int[][] directions = {
//                {-1, 0}, // up
//                {1, 0},  // down
//                {0, -1}, // left
//                {0, 1}   // right
//        };
//
//        for (int[] direction : directions) {
//            int currentRow = row;
//            int currentCol = col;
//
//            while (true) {
//                currentRow += direction[0];
//                currentCol += direction[1];
//
//                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
//                    break; // Out of bounds
//                }
//
//                ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
//                ChessPiece targetPiece = board.getPiece(newPosition);
//
//                if (targetPiece == null) {
//                    validMoves.add(new ChessMove(myPosition, newPosition, null)); // Empty square
//                } else {
//                    if (targetPiece.getTeamColor() != piece.getTeamColor()) {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null)); // Capture opponent's piece
//                    }
//                    break; // Stop at the first piece encountered
//                }
//            }
//        }
//
//    }
//
//    private void pawn(ChessPiece piece, ChessPosition myPosition, ChessBoard board, int row, int col, Collection<ChessMove> validMoves) {
//        ChessPosition newPosition; // New position for the pawn
//        PieceType[] promotionTypes = {PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP}; // Promotion types
//
//        int direction = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? -1 : 1; // Direction of the pawn
//        int startRow = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 7 : 2; // Starting row of the pawn
//        int promotionRow = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 2 : 7; // Promotion row of the pawn
//        boolean canPromote = row == promotionRow;
//
//        // Move forward
//        newPosition = new ChessPosition(row + direction, col);
//        if (board.getPiece(newPosition) == null) {
//            if (canPromote) {
//                for (PieceType type : promotionTypes) {
//                    validMoves.add(new ChessMove(myPosition, newPosition, type));
//                }
//            } else {
//                validMoves.add(new ChessMove(myPosition, newPosition, null));
//                if (row == startRow) {
//                    newPosition = new ChessPosition(row + 2 * direction, col);
//                    if (board.getPiece(newPosition) == null) {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null));
//                    }
//                }
//            }
//        }
//
//        // Capture diagonally
//        int[] diagonalDirections = {1, -1}; // Diagonal directions
//        // Loop through the diagonal directions
//        for (int diagonalDirection : diagonalDirections) {
//            newPosition = new ChessPosition(row + direction, col + diagonalDirection);
//            if (cover(newPosition)) {
//                ChessPiece targetPiece = board.getPiece(newPosition);
//                if (targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) {
//                    if (canPromote) {
//                        for (PieceType type : promotionTypes) {
//                            validMoves.add(new ChessMove(myPosition, newPosition, type));
//                        }
//                    } else {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null));
//                    }
//                }
//            }
//        }
//    }



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
