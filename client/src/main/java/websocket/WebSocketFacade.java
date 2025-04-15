package websocket;


import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import websocket.messages.ServerMessage;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            // Ensure the URL is correctly formatted for WebSocket
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            //System.out.println("Connecting to WebSocket URI: " + socketURI); // Debugging log
            this.session = container.connectToServer(this, socketURI);

            // Set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notificationMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.handle(message); // Pass the parsed object instead of the raw message
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.err.println("WebSocket connection failed: " + ex.getMessage()); // Debugging log
            throw new ResponseException(500, "WebSocket failed: " + ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session; // Properly initialize the session
    }

    public void connect(String auth, int gameID) throws ResponseException {
        try {
            var cmd = new ConnectCommand(auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    public void leaveGame(AuthData auth, int gameID) throws ResponseException {
        try {
            var cmd = new LeaveCommand(auth.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resignGame(AuthData auth, int gameID) throws ResponseException {
        try {
            var cmd = new ResignCommand(auth.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(AuthData auth, int gameID, ChessMove move) throws ResponseException {
        try {
            var cmd = new MakeMoveCommand(auth.authToken(), gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

//    public void leaveGame(String authData, String authData2) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'leaveGame'");
//    }


}
