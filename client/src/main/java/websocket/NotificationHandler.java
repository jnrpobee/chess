package websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void handle(String message);
    //void handle(ServerMessage notificationMessage);
}

