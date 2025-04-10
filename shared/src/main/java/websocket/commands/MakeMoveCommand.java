package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{

    ChessMove move;
    String startPos;
    String endPos;
    String playerColor;

    public ChessMove getMove() {
        return move;
    }

    public String getStartPos() {
        return startPos;
    }

    public String getEndPos() {
        return endPos;
    }

    public String getPlayerColor(){
        return playerColor;
    }

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String startPos, String endPos, String playerColor) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.startPos = startPos;
        this.endPos = endPos;
        this.playerColor = playerColor;
    }
}
