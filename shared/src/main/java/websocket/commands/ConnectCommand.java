package websocket.commands;

import chess.ChessMove;

public class ConnectCommand extends UserGameCommand{

    String playerColor;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String playerColor) {
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
    }
}
