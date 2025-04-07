package websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void handle(String message);
}
//public interface NotificationHandler {
//    void notify(NotificationMessage message);
//
//    void loadGame(LoadGameMessage message);
//
//    void error(ErrorMessage message);
//}
