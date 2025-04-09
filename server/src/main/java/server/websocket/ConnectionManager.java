package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Integer gameID, Session session) {
        var connection = new Connection(gameID, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, Integer gameID, ServerMessage notification) throws IOException {
        ArrayList<String> removeList = new ArrayList<>();
        for (var entry : connections.entrySet()) {
            var c = entry.getValue();
            if (!excludeVisitorName.equals(entry.getKey())) {
                if (c.session.isOpen()) {
                    if (c.gameID.equals(gameID)) {
                        c.send(notification.toString());
                    }
                } else {
                    removeList.add(entry.getKey());
                }
            }
        }

        // Clean up any connections that were left open.
        for (String visitorName : removeList) {
            connections.remove(visitorName);
        }
    }

    public void sendGame(String username, ChessGame game) throws IOException {
        var c = connections.get(username);
        if (c.session.isOpen()) {
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            String gameJson = new Gson().toJson(notification);
            c.send(gameJson);
        } else {
            connections.remove(username);
        }
    }

    public void sendError(Session session, String msg) throws IOException {
        if (session.isOpen()){
            var errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,msg);
            session.getRemote().sendString(errorNotification.toString());
        }else {
            session.close();
        }
    }


    public boolean containsPair(String visitorName, Connection connection) {
        // Iterate through all entries in the ConcurrentHashMap
        for (var entry : connections.entrySet()) {
            String key = entry.getKey();
            Connection value = entry.getValue();

            // Check if both key and value match
            if (key.equals(visitorName) && value.equals(connection)) {
                return true; // Pair found
            }
        }
        return false; // Pair not found
    }

}
