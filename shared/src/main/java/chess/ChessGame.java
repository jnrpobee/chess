package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        NONE
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new HashSet<>(0);

        if (board.getPiece(startPosition) == null) {
            return validMoves;
        } else {
            validMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> acceptableMove = new HashSet<>(0);
            for (var potentialMove : validMoves) {
                ChessPiece piece = board.getPiece(potentialMove.getStartPosition());
                ChessPiece targetPiece = board.getPiece(potentialMove.getEndPosition());

                board.addPiece(potentialMove.getEndPosition(), piece);
                board.removePiece(potentialMove.getStartPosition());

                if (!this.isInCheck(piece.getTeamColor())) {
                    acceptableMove.add(potentialMove);
                }

                board.addPiece(potentialMove.getStartPosition(), piece);
                board.addPiece(potentialMove.getEndPosition(), targetPiece);
            }
            return acceptableMove;
        }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        //checks for the corrects team's turn
        if (piece.getTeamColor() != this.teamTurn) {
            throw new InvalidMoveException("wait for your turn");
        }

        if (piece.pieceMoves(board, move.getStartPosition()).stream().noneMatch(M -> M.getEndPosition().equals(move.getEndPosition()))) {
            throw new InvalidMoveException(" An invalid move");
        }

        ChessPiece targetPiece = board.getPiece(move.getEndPosition());

        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());

        if (isInCheck(piece.getTeamColor())) {
            board.addPiece(move.getStartPosition(), piece);
            board.addPiece(move.getEndPosition(), targetPiece);
            throw new InvalidMoveException("This will put the king in check");
        }

        if (targetPiece != null) {
            if (targetPiece.getPieceType() == ChessPiece.PieceType.KING) {
                throw new InvalidMoveException("can't capture the king");
            }
        }
        //handing promotion
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            this.board.addPiece(move.getEndPosition(), piece);
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingsPlace = null;
        ChessPosition currentPosition;
        ChessPiece piece;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    System.out.println("king of team " + teamColor.toString() + " found ");
                    kingsPlace = currentPosition;
                    break;
                }
                //System.out.println(" null " );
            }
            if (kingsPlace != null)
            {
                System.out.println("king place not equal to null " );
                break;
            }
        }

        if (kingsPlace == null) {
            System.out.println("king place to null " );
            throw new RuntimeException("King not found");
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    System.out.println("king of team " + teamColor.toString() );
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : moves) {
                        //System.out.println("\nChecking move: " + move.toString() + " {start(" + move.getStartPosition().getRow()  + ", " +  move.getStartPosition().getColumn()  + ") end(" +  move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn() + ")}");
                        if (move.getEndPosition().equals(kingsPlace)) {
                            System.out.println("\nChecking move: " + move.toString() + " {start(" + move.getStartPosition().getRow()  + ", " +  move.getStartPosition().getColumn()  + ") end(" +  move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn() + ")}");
                            return true;

                        }
                        //System.out.println(" return true " );
                    }
                }
            }
        }

        return false;
//        throw new RuntimeException("Not implemented");
        
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        ChessPosition currentPosition;
        ChessPiece piece;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : moves) {
                        ChessPiece targetPiece = board.getPiece(move.getEndPosition());

                        board.addPiece(move.getEndPosition(), piece);
                        board.removePiece(move.getStartPosition());

                        if (!isInCheck(teamColor)) {
                            board.addPiece(move.getStartPosition(), piece);
                            board.addPiece(move.getEndPosition(), targetPiece);
                            return false;
                        }

                        board.addPiece(move.getStartPosition(), piece);
                        board.addPiece(move.getEndPosition(), targetPiece);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        if (isInCheck(teamColor)) {
            return false; // A team in check cannot be in stalemate
        }
        ChessPosition currentPosition;
        ChessPiece piece;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : moves) {
                        ChessPiece targetPiece = board.getPiece(move.getEndPosition());

                        board.addPiece(move.getEndPosition(), piece);
                        board.removePiece(move.getStartPosition());

                        if (!isInCheck(teamColor)) {
                            board.addPiece(move.getStartPosition(), piece);
                            board.addPiece(move.getEndPosition(), targetPiece);
                            return false;
                        }
                        board.addPiece(move.getStartPosition(), piece);
                        board.addPiece(move.getEndPosition(), targetPiece);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {

        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }
}
