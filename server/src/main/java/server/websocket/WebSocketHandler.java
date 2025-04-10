package server.websocket;

import chess.*;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


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
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove( username, msg);
                case LEAVE -> leave( username,  command );
                case RESIGN -> resign( username,  command );
            }
        } catch (DataAccessException ex) {
            // Serializes and sends the error message
            connections.sendError(session,ex.getMessage());
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
        if (gData != null && username.equals(gData.whiteUsername())){
            playerColor = "white";
            message = String.format("%s is now playing as %s", username, playerColor);
        } else if (gData != null && username.equals(gData.blackUsername())) {
            playerColor = "black";
            message = String.format("%s is now playing as %s",username,playerColor);
        } else {
            message = String.format("%s is now watching as an observer",username);
        }
        // send LOAD_GAME message back to the root client including the current game state
        connections.sendGame(username,gData.game());
        // broadcast to remaining player and observers
        connections.broadcast(username,gID,message);
    }

    private void makeMove(String username, String msg) throws DataAccessException, InvalidMoveException, IOException {
        // deserialize command
        MakeMoveCommand mmcmd = new Gson().fromJson(msg,MakeMoveCommand.class);
        GameData gData = gDAO.getGame(mmcmd.getGameID());
        // verify validity of move
        if(gData.game().gameOver){
            throw new InvalidMoveException("Error: the game has ended and no more moves can be made.");
        }
        ChessGame.TeamColor movingColor;
        if (gData.whiteUsername().equals(username)){
            movingColor = ChessGame.TeamColor.WHITE;
        } else{
            movingColor = ChessGame.TeamColor.BLACK;
        }
        if(gData.game().getTeamTurn() != movingColor){
            throw new InvalidMoveException("Error: it is not your turn to move.");
        }
        ChessGameValidMoves validMovesObj = new ChessGameValidMoves(gData.game());
        Collection<ChessMove> validMovesList = validMovesObj.validMoves(mmcmd.getMove().getStartPosition());
        ChessMove move = mmcmd.getMove();
        if(!validMovesList.contains(move)) {
            throw new InvalidMoveException("Error: invalid move! Try \"check <PiecePosition>\" to see valid moves for a particular piece");
        }
        // update game to represent move, then update database
        gData.game().makeMove(move);
        String json = new Gson().toJson(gData.game());
        gDAO.updateGameJSON(mmcmd.getGameID(),json);

        // send LOAD_GAME msg to all clients in the game
        connections.sendGame(username,gData.game());
        connections.broadcastGame(username,gData.gameID(),gData.game());

        ArrayList<String> coordList = stringForm(move);
        // send a notification to all other clients informing them what move was made
        String message = String.format("%s moved %s to %s",username, coordList.get(0),coordList.get(1));
        connections.broadcast(username, mmcmd.getGameID(), message);

        // if the move results in check, checkmate, or stalemate server sends another notification to all clients

        if (gData.game().gameOver && gData.game().isInCheckmate(movingColor)){
            msg = String.format("CHECKMATE\nCongratulations %s!",username);
            connections.broadcast(null, mmcmd.getGameID(), msg);
        } else if(gData.game().gameOver && gData.game().isInStalemate(movingColor)){
            msg = "STALEMATE\nGreat game!";
            connections.broadcast(null, mmcmd.getGameID(), msg);
        } else if(gData.game().whiteCheck){
            msg = String.format("CHECK : %s (white) is in check!",gData.whiteUsername());
            connections.broadcast(null, mmcmd.getGameID(), msg);
        } else if(gData.game().blackCheck){
            msg = String.format("CHECK : %s (black) is in check!",gData.blackUsername());
            connections.broadcast(null, mmcmd.getGameID(), msg);
        }

    }

    private ArrayList<String> stringForm(ChessMove move) {
        ArrayList<String> returnList = new ArrayList<>();

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        int endRow = endPos.getRow();
        int endCol = endPos.getColumn();

        if (startRow < 1 || startRow > 8 || startCol < 1 || startCol > 8
        || endRow < 1 || endRow > 8 || endCol < 1 || endCol > 8) {
            throw new IllegalArgumentException("Row values must be from a - h\nColumn values must be from 1 - 8.");
        }

        char startRowChar = (char) ('a' + (startRow - 1));
        char endRowChar = (char) ('a'+ (endRow - 1));

        String startString = String.valueOf(startRowChar)+startCol;
        String endString = String.valueOf(endRowChar)+endCol;
        returnList.add(startString);
        returnList.add(endString);

        return returnList;
    }

    private void leave(String username, UserGameCommand command) throws DataAccessException, IOException {
        // close connection
        connections.remove(username);
        // update game in Database
        GameData gData = gDAO.getGame(command.getGameID());
        var message = "";
        if (username.equals(gData.whiteUsername())){
            gDAO.leaveGame("WHITE", command.getGameID());
            message = String.format("%s has stopped playing as white",username);
        } else if (username.equals(gData.blackUsername())) {
            gDAO.leaveGame("BLACK", command.getGameID());
            message = String.format("%s has stopped playing as black",username);
        } else {
            message = String.format("%s is no longer observing the game",username);
        }
        // broadcast to all other clients
        connections.broadcast(username,command.getGameID(),message);
    }

    private void resign(String username, UserGameCommand command) throws DataAccessException, IOException {

        GameData gData = gDAO.getGame(command.getGameID());
        if (gData.game().gameOver) {
            throw new IOException("Game is over, can't resign");
        }
        // mark the game as over
        gData.game().gameOver = true;
        String json = new Gson().toJson(gData.game());
        // update the game in the database
        gDAO.updateGameJSON(gData.gameID(), json);

        // broadcast to all clients
        if (username.equals(gData.whiteUsername())) {
            var message = String.format("%s has resigned. Black wins!", username);
            connections.broadcast(username, command.getGameID(), message);
            connections.sendMessage(username,message);
        } else if (username.equals(gData.blackUsername())) {
            var message = String.format("%s has resigned. White wins!", username);
            connections.broadcast(username, command.getGameID(), message);
            connections.sendMessage(username,message);
        } else{
            throw new IOException("Observer can't resign");
        }

    }

    private void saveSession(Integer gameID, String username, Session session) {
        Connection testConnection = new Connection(gameID, session);
        if(!connections.containsPair(username,testConnection)){
            connections.add(username,gameID,session);
        }
    }


}
