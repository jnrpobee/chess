package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(ServerMessageType type, String message) {
//        super(ServerMessageType.NOTIFICATION);
//        this.message = message;
        super(type);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
