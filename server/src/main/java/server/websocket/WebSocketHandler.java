package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO aDAO;
    private final GameDAO gDAO;

    public WebSocketHandler(AuthDAO aDAO, GameDAO gDAO){
        this.aDAO = aDAO;
        this.gDAO = gDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

            AuthData aData = aDAO.getAuth(command.getAuthToken());
            String username = aData.username();

            saveSession(command.getGameID(), username, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command); //(ConnectCommand)
                case MAKE_MOVE -> makeMove( username, command); //(MakeMoveCommand)
                case LEAVE -> leave( username,  command ); //(LeaveGameCommand)
                case RESIGN -> resign( username,  command ); //(ResignCommand)
            }
        } catch (DataAccessException ex) {
            // Serializes and sends the error message
            connections.sendError(session,"Error: unauthorized");
        } catch (Exception ex) {
            ex.printStackTrace();
            connections.sendError(session,"Error: " + ex.getMessage());
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {

        var gID = command.getGameID();
        connections.add(username, gID, session);
        GameData gData = gDAO.getGame(gID);
        String playerColor;
        var message = "";
        if (username.equals(gData.whiteUsername())){
            playerColor = "white";
            message = String.format("%s is now playing as &s", username, playerColor);
        } else if (username.equals(gData.blackUsername())) {
            playerColor = "black";
            message = String.format("%s is now playing as &s",username,playerColor);
        } else {
            message = String.format("%s is now watching as an observer",username);
        }
        // send LOAD_GAME message back to the root client including the current game state
        connections.sendGame(username,gData.game());
        // broadcast to remaining player and observers
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username,gID,notification);
    }

    private void makeMove(String username, UserGameCommand command){
        // verify validity of move
        // update game to represent move, then update database
        // send LOAD_GAME msg to all clients in the game
        // send a notification to all other clients informing them what move was made
        // if the move results in check, checkmate, or stalemate server sends another notification to all clients
    }

    private void leave(String username, UserGameCommand command) throws DataAccessException, IOException {
        // close connection
        connections.remove(username);
        // update game in Database
        GameData gData = gDAO.getGame(command.getGameID());
        var message = "";
        if (username.equals(gData.whiteUsername())){
            gDAO.updateGame(null,"WHITE", command.getGameID());
            message = String.format("%s has stopped playing as white",username);
        } else if (username.equals(gData.blackUsername())) {
            gDAO.updateGame(null,"BLACK", command.getGameID());
            message = String.format("%s has stopped playing as black",username);
        } else {
            message = String.format("%s is no longer observing the game",username);
        }
        // broadcast to all other clients
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username,command.getGameID(),notification);
    }

    private void resign(String username, UserGameCommand command) throws DataAccessException, IOException {
        // mark the game as over
        // update the game in the database
        // broadcast to all clients
        GameData gData = gDAO.getGame(command.getGameID());
        if (username.equals(gData.whiteUsername())) {
            String otherUser = String.format(gData.blackUsername());
        } else if (username.equals(gData.blackUsername())) {
            String otherUser = String.format(gData.whiteUsername());
            var message = String.format("%s has resigned. Congratulations &s!", username, otherUser);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(username, command.getGameID(), notification);
        }
    }

    private void saveSession(Integer gameID, String username, Session session) {
        Connection testConnection = new Connection(gameID, session);
        if(!connections.containsPair(username,testConnection)){
            connections.add(username,gameID,session);
        }
    }


}
