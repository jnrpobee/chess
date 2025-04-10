package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import exception.ResponseException;
import dataaccess.mysql.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import model.*;
import dataaccess.service.*;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.Objects;

import javax.management.Notification;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService;
    private final LoginService loginService;

    public WebSocketHandler(GameService gameService, LoginService loginService) {
        this.gameService = gameService;
        this.loginService = loginService;


    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
            UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(obj.get("commandType").getAsString());
            int gameID = (obj.get("gameID").getAsInt());
            switch (type) {
                case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leaveGame(new Gson().fromJson(message, LeaveCommand.class));
                case RESIGN -> resignGame(new Gson().fromJson(message, ResignCommand.class));
            }
        } catch (Exception e) {
            System.out.printf("Error occurred: %s%n", e.getMessage());
            var errorMessage = new ErrorMessage(null, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void resignGame(ResignCommand command) throws DataAccessException, IOException, ResponseException {
        String username = loginService.getUser(command.getAuthString());
        var gameData = this.gameService.getGame(command.getGameID());

        if (!Objects.equals(((GameData) gameData).whiteUsername(), username) && !Objects.equals(((GameData) gameData).blackUsername(), username)) {
            throw new ResponseException(401, "Can't resign as an observer");
        }
        if (((GameData) gameData).game().getTeamTurn() == ChessGame.TeamColor.NONE) {
            throw new ResponseException(400, "Already resigned");
        }

        ((GameData) gameData).game().setTeamTurn(ChessGame.TeamColor.NONE);
        gameService.updateGame((GameData) gameData);

        var message = new NotificationMessage(null, String.format("Player %s has resigned the game.", username));
        connections.broadcast(command.getGameID(), null, message);
    }

    private void leaveGame(LeaveCommand leaveCommand) throws DataAccessException, IOException {
        String username = loginService.getUser(leaveCommand.getAuthString());
        connections.remove(username);
        var notification = new NotificationMessage(null, String.format("Player %s has left the game.", username));
        int gameID = leaveCommand.getGameID(); // Assuming LeaveCommand has a getGameID() method
        connections.broadcast(gameID, username, notification);
    }


    public void makeMove(MakeMoveCommand command) throws ResponseException, DataAccessException {
        int gameID = command.getGameID();
        String auth = command.getAuthString();
        String username = loginService.getUser(auth);
        ChessGame game = ((GameData) gameService.getGame(gameID)).getGame();
        GameData gameData = (GameData) gameService.getGame(gameID);
        ChessMove move = command.getMove();
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());

        if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && !(Objects.equals(gameData.whiteUsername(), username))) ||
                (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !(Objects.equals(gameData.blackUsername(), username)))) {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: not your turn"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        if (game.getTeamTurn() == ChessGame.TeamColor.NONE) {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game is over"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            try {
                var invalidMoveMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid move");
                connections.sendError(username, invalidMoveMessage);
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }

        // Notify all clients about the move
        var notificationMessage = String.format("Player %s has moved %s from %s to %s", username, piece.getPieceType().toString(), convertPos(move.getStartPosition()), convertPos(move.getEndPosition()));
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);

        try {
            connections.broadcast(gameID, auth, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to broadcast move notification: " + e.getMessage());
        }

        // Send updated game state to all clients
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        try {
            connections.sendLoadCommand(gameID, loadGameMessage);
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to send updated game state: " + e.getMessage());
        }

        // Check for check or checkmate and notify players if necessary
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            game.setTeamTurn(null);
            setGame(gameID, new AuthData(auth, auth), game);
            var endGameNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "White wins!");
            try {
                connections.broadcast(gameID, null, endGameNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            game.setTeamTurn(null);
            setGame(gameID, new AuthData(auth, auth), game);
            var endGameNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Black wins!");
            try {
                connections.broadcast(gameID, null, endGameNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Black is in check!");
            try {
                connections.broadcast(gameID, null, checkNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "White is in check!");
            try {
                connections.broadcast(gameID, null, checkNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
    }

    public String convertPos(ChessPosition pos)
    {
        String str = "";
        switch (pos.getColumn())
        {
            case 1: {
                str += "a";
                break;
            }
            case 2: {
                str += "b";
                break;
            }
            case 3: {
                str += "c";
                break;
            }
            case 4: {
                str += "d";
                break;
            }
            case 5: {
                str += "e";
                break;
            }
            case 6: {
                str += "f";
                break;
            }
            case 7: {
                str += "g";
                break;
            }
            case 8: {
                str += "h";
                break;
            }
        }
        str += (pos.getRow());
        return str;
    }






    public void connect(ConnectCommand connectCommand, Session session) throws DataAccessException, IOException {
        String username;
        GameData gameData;

        try {
            username = loginService.getUser(connectCommand.getAuthString());
            gameData = (GameData) this.gameService.getGame(connectCommand.getGameID());
        } catch (Exception e) {
            // Send LOAD_GAME message with error details to root client
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage());
            // Removed unused variable loadGameMessage
            // Include error message in the serialized JSON instead of calling a non-existent method
            connections.sendMessageToSession(session, new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.LOAD_GAME, errorMessage.getErrorMessage())));
            return;
        }

        // Check if the user is allowed to rejoin the game
        if (gameData.game().getTeamTurn() == ChessGame.TeamColor.NONE) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Cannot rejoin a game that has ended.");
            connections.sendMessageToSession(session, new Gson().toJson(errorMessage));
            return;
        }

        connections.add(connectCommand.getGameID(), username, session);

        // Send LOAD_GAME message to root client
        sendGameToSession(gameData, session);

        // Determine if the user is joining as a player or observer
        ChessGame.TeamColor playerColor = null;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        }

        // Send Notification to other clients
        String role = (playerColor != null) ? playerColor.toString() : "an observer";
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("Player %s has joined as %s.", username, role));
        connections.broadcast(connectCommand.getGameID(), username, notification);
    }

    private void sendGameToSession(GameData game, Session session) throws IOException {
        var message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
        //message.setPlayerColor(game.game().getTeamTurn());
        connections.sendMessageToSession(session, new Gson().toJson(message));
    }

    private String getUsername(Object gameData, ChessGame.TeamColor playerColor) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            return ((GameData) gameData).whiteUsername();
        } else {
            return ((GameData) gameData).blackUsername();
        }
    }

    public void setGame(int gameID, AuthData auth, ChessGame game) throws DataAccessException, ResponseException {
        // Validate if the game exists
        GameData gameData = (GameData) gameService.getGame(gameID);
        if (gameData == null) {
            throw new ResponseException(400, "No game with that ID");
        }

        // Validate authorization
        if (!gameData.whiteUsername().equals(auth.username()) && !gameData.blackUsername().equals(auth.username())) {
            throw new ResponseException(401, "Unauthorized to update this game");
        }

        // Validate if the game is in progress
        if (game.getTeamTurn() == ChessGame.TeamColor.NONE) {
            throw new ResponseException(400, "Game is already over");
        }

        // Update the game in the service
        gameService.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));

        // Notify all clients about the game update
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("Game %d has been updated.", gameID));
        try {
            connections.broadcast(gameID, null, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to broadcast game update: " + e.getMessage());
        }
    }

}
