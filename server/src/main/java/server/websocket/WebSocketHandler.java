package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
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
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

            // Throws a custom UnauthorizedException. Yours may work differently.

            String username = getUsername(command);

            saveSession(command.getGameID(), username, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command); //(ConnectCommand)
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username,  command); //(LeaveGameCommand)
                case RESIGN -> resign(session, username,  command); //(ResignCommand)
            }
        } catch (UnauthorizedException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {

        var gID = command.getGameID();
        connections.add(username, gID, session);
        GameData gData = gDAO.getGame(gID);
        String playerColor;
        if (username.equals(gData.whiteUsername())){
            playerColor = "white";
        } else if (username.equals(gData.blackUsername())) {
            playerColor = "black";
        } else {
            throw new RuntimeException();
        }
        // send LOAD_GAME message back to the root client including the current game state
        connections.sendGame(username,gData.game());
        // broadcast to remaining player and observers
        var message = String.format("%s is now playing as &s",username,playerColor);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username,gID,notification);
    }

    private void saveSession(Integer gameID, String username, Session session) {
        Connection testConnection = new Connection(gameID, session);
        if(!connections.containsPair(username,testConnection)){
            connections.add(username,gameID,session);
        }
    }

    private String getUsername(UserGameCommand command) throws DataAccessException {
        AuthData aData = aDAO.getAuth(command.getAuthToken());
        return aData.username();
    }

}
