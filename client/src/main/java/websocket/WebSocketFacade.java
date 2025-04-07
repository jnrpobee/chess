package websocket;


import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
//import service.AuthData;
import model.AuthData;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler NotificationHandler;


    public WebSocketFacade(String url, NotificationHandler NotificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.NotificationHandler = NotificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    NotificationHandler.handle(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

//    public void connect(AuthData auth, int gameID, ChessGame.TeamColor teamColor) throws ResponseException {
//        try {
//            var cmd = new connectCommand(auth.getAuthToken(), gameID, teamColor);
//            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
////    public void joinObserver(AuthData auth, int gameID) throws ResponseException {
////        try {
////            var cmd = new JoinObserverCommand(auth.getAuthToken(), gameID);
////            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
////        } catch (IOException ex) {
////            throw new ResponseException(500, ex.getMessage());
////        }
////    }
//
//    public void leaveGame(AuthData auth, int gameID) throws ResponseException {
//        try {
//            var cmd = new LeaveCommand(auth.getAuthToken(), gameID);
//            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
//        } catch (IOException e) {
//            throw new ResponseException(500, e.getMessage());
//        }
//    }
//
//    public void resignGame(AuthData auth, int gameID) throws ResponseException {
//        try {
//            var cmd = new ResignCommand(auth.getAuthToken(), gameID);
//            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
//        } catch (IOException e) {
//            throw new ResponseException(500, e.getMessage());
//        }
//    }
//
//    public void makeMove(AuthData auth, int gameID, ChessMove move) throws ResponseException {
//        try {
//            var cmd = new MakeMoveCommand(auth.getAuthToken(), gameID, move);
//            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
//        } catch (IOException e) {
//            throw new ResponseException(500, e.getMessage());
//        }
//    }
//
//
}
//
