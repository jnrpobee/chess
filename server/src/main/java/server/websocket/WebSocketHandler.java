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
        connections.broadcast(0, null, message);
    }

    private void leaveGame(LeaveCommand leaveCommand) throws DataAccessException, IOException {
        String username = loginService.getUser(leaveCommand.getAuthString());
        connections.remove(username);
        var notification = new NotificationMessage(null, String.format("Player %s has left the game.", username));
        int gameID = leaveCommand.getGameID(); // Assuming LeaveCommand has a getGameID() method
        connections.broadcast(gameID, username, notification);
    }


    public void makeMove(MakeMoveCommand command) throws ResponseException, DataAccessException {
        //MakeMoveCommand cmd = new Gson().fromJson(message, MakeMoveCommand.class);
        int gameID = command.getGameID();
        String auth = command.getAuthString();
        String username = loginService.getUser(auth);
        ChessGame game = ((GameData) gameService.getGame(gameID)).getGame();
        GameData gameData = (GameData) gameService.getGame(gameID);
        ChessMove move = command.getMove();
        //ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());


        if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && !(Objects.equals(gameData.blackUsername(), username))) ||
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
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid move"));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }
    }


    public void connect(ConnectCommand connectCommand, Session session) throws DataAccessException, IOException {
        String username = loginService.getUser(connectCommand.getAuthString());
        connections.add(0, username, session);
        var gameData = this.gameService.getGame(connectCommand.getGameID());
        if (!username.equals(getUsername(gameData, connectCommand.getPlayerColor()))) {
            var message = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Can't join as " + connectCommand.getPlayerColor().toString());
            connections.sendMessage(0, new Gson().toJson(message));
        } else {
            var text = String.format("Player %s has joined as %s%n", username, connectCommand.getPlayerColor());
            var notification = new NotificationMessage(null, text);
            connections.broadcast(0, username, notification);
            sendGame((GameData) gameData, connectCommand.getPlayerColor(), username);
        }
    }

    private String getUsername(Object gameData, ChessGame.TeamColor playerColor) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            return ((GameData) gameData).whiteUsername();
        } else {
            return ((GameData) gameData).blackUsername();
        }
    }

    public void sendGame(GameData game, ChessGame.TeamColor color, String player) throws IOException {
        var message = new LoadGameMessage(game);
        message.setColor(color);
        connections.sendMessage(0, new Gson().toJson(message));
    }


}
