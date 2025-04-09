package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;

    public ChessGame getGame() {
        return game;
    }

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
