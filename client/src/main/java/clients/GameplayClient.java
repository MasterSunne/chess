package clients;

import chess.ChessGame;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayClient {
    private ClientData clientData;
    private final ServerFacade server;

    public GameplayClient(String serverUrl, ClientData cd) {
        server = new ServerFacade(serverUrl);
        this.clientData = cd;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!clientData.getIsObserver()) {
                return switch (cmd) {
//                    case "move" -> move(clientData, params);
                    case "redraw" -> redraw(clientData);
//                    case "check" -> check();
//                    case "resign" -> resign(clientData);
//                    case "leave" -> leave(clientData);
                    case "help" -> help(clientData);
                    default -> help(clientData);
                };
            } else {
                return switch (cmd) {
                    case "redraw" -> redraw(clientData);
//                    case "check" -> check();
//                    case "leave" -> leave(clientData);
                    case "help" -> help(clientData);
                    default -> help(clientData);
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String redraw(ClientData clientData){
        if(!clientData.getIsObserver()){
            if(clientData.getPlayerColor().equals("white")){
                DrawBoard.main(ChessGame.TeamColor.WHITE,clientData.getGameBoard());
            } else if (clientData.getPlayerColor().equals("black")){
                DrawBoard.main(ChessGame.TeamColor.BLACK,clientData.getGameBoard());
            }
        } else {
            DrawBoard.main(ChessGame.TeamColor.WHITE,clientData.getGameBoard());
        }
        return "";
    }

    public String help(ClientData clientData) {
        if (!clientData.getIsObserver()) {
            return RESET_TEXT_COLOR + """
                    move <START_POSITION> <END_POSITION> - a piece
                    redraw - the chess board
                    check <PIECE_POSITION> - a piece's legal moves
                    resign - to forfeit the game
                    leave - the game without resigning
                    help  - for possible commands
                    """;
        } else {
            return RESET_TEXT_COLOR + """
                    redraw - the chess board
                    check <PIECE_POSITION> - a piece's legal moves
                    leave - the game
                    help  - for possible commands
                    """;
        }

    }

}
