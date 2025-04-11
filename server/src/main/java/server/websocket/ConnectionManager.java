package server.websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();


    public void add(int gameID, String authToken, Session session) {
        var connection = new Connection(authToken, session);
        Set<Connection> tmp = connections.get(gameID);
        if (tmp == null) {
            Set<Connection> tmp2 = new HashSet<>();
            tmp2.add(connection);
            connections.put(gameID, tmp2);
        } else {
            tmp.add(connection);
            connections.put(gameID, tmp);
        }
    }

    public void removeSessionFromGame(int gameID, String authToken) {
        Set<Connection> tmp = connections.get(gameID);
        tmp.removeIf(conn -> Objects.equals(conn.authToken, authToken));
    }


    public void sendMessageToSession(Session session, String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }

    public void broadcast(int gameID, String excludeUser, NotificationMessage notification) throws IOException {
        var removeList = new HashSet<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeUser)) {
                    String msg = new Gson().toJson(notification, NotificationMessage.class);
                    c.send(msg);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            Set<Connection> tmp = connections.get(gameID);
            tmp.remove(c);
            connections.put(gameID, tmp);
        }
    }

    public void sendLoadCommand(int gameID, LoadGameMessage loadGameMessage) throws IOException {
        var removeList = new HashSet<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                String msg = new Gson().toJson(loadGameMessage, LoadGameMessage.class);
                c.send(msg);
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            Set<Connection> tmp = connections.get(gameID);
            tmp.remove(c);
            connections.put(gameID, tmp);
        }
    }


    public void sendError(String authToken, ErrorMessage errorMessage) throws IOException {
        var removeList = new HashSet<Connection>();
        for (int gameID : connections.keySet()) {
            for (var c : connections.get(gameID)) {
                if (c.session.isOpen()) {
                    if (c.authToken.equals(authToken)) {
                        String msg = new Gson().toJson(errorMessage, ErrorMessage.class);
                        c.send(msg);
                    }
                } else {
                    removeList.add(c);
                }
            }

            // Clean up any connections that were left open.
            for (var c : removeList) {
                Set<Connection> tmp = connections.get(gameID);
                tmp.remove(c);
                connections.put(gameID, tmp);
            }
        }
    }

    public void remove(String userName) {
        connections.remove(userName);
    }


}
