package websocket;

import chess.ChessMove;
import clients.ClientData;
import com.google.gson.Gson;
import server.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
//                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    public void connect(ClientData clientData) throws ResponseException {
        try {
            var connectCmd = new ConnectCommand(UserGameCommand.CommandType.CONNECT, clientData.getAuthToken(),
                    clientData.getGameID(),clientData.getPlayerColor());
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(ClientData clientData, ChessMove move) throws ResponseException {
        try {
            var makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, clientData.getAuthToken(),
                    clientData.getGameID(),move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(ClientData clientData) throws ResponseException {
        try {
            var leaveCmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, clientData.getAuthToken(),
                    clientData.getGameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(ClientData clientData) throws ResponseException {
        try {
            var resignCmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN, clientData.getAuthToken(),
                    clientData.getGameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
