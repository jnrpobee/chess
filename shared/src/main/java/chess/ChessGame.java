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

        if (board.getPiece(startPosition) == null){
            return validMoves;
        }else{
           validMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> acceptableMove = new HashSet<>(0);
            for(var potentialMove : validMoves){
                ChessPiece piece = board.getPiece(potentialMove.getStartPosition());
                ChessPiece targetPiece = board.getPiece(potentialMove.getEndPosition());

                board.addPiece(potentialMove.getEndPosition(), piece);
                board.removePiece(potentialMove.getStartPosition());

                if(!this.isInCheck(piece.getTeamColor())){
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
            board.removePiece(move.getEndPosition());
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
        ChessPosition kingPlace = null;
        ChessPosition currentPosition;
        ChessPiece piece;
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                currentPosition = new ChessPosition(i, j);
                piece = board.getPiece(currentPosition);
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    kingPlace = currentPosition;
                    break;
                }
            }
            if (kingPlace != null){
                break;
            }
        }

        if(kingPlace == null){
            throw new RuntimeException("King not found");
        }

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                currentPosition = new ChessPosition(i, j);
                piece = board.getPiece(currentPosition);
                if(piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : moves) {
                        
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

        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        ChessPosition myPosition;
//        if (teamColor == TeamColor.WHITE) {
//            for (int i = 1; i <= 8; i++) {
//                for (int j = 1; j <= 8; j++) {
//                    myPosition = new ChessPosition(i, j);
//                    if (board.getPiece(myPosition) != null && board.getPiece(myPosition).getTeamColor() == TeamColor.WHITE) /*? TeamColor.BLACK : TeamColor.WHITE */{
//                        if (board.getPiece(myPosition).validMoves(myPosition, board).size() != 0) {
//                            return false;
//                        }
//                    }
//                }
//            }
//        } else {
//            for (int i = 1; i <= 8; i++) {
//                for (int j = 1; j <= 8; j++) {
//                    myPosition = new ChessPosition(i, j);
//                    if (board.getPiece(myPosition) != null && board.getPiece(myPosition).getTeamColor() == TeamColor.BLACK) {
//                        if (board.getPiece(myPosition).validMoves(myPosition, board).size() != 0) {
//                            return false;
//                        }
//                    }
//                }
//            }
//        }
        throw new RuntimeException("Not implemented");
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
