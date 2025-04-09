package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    String gameJSON;

    public LoadGameMessage(ServerMessageType type, String gameJSON) {
        super(type);
        this.gameJSON = gameJSON;
    }
}
