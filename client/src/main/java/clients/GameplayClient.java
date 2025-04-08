package clients;

import chess.ChessGame;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayClient {
    private final Repl repl;
    private final ServerFacade server;

    public GameplayClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!repl.getObserver()) {
                return switch (cmd) {
                    case "move" -> move(repl, params);
                    case "redraw" -> redraw(repl);
                    case "check" -> check();
                    case "resign" -> resign(repl);
                    case "leave" -> leave(repl);
                    case "help" -> help(repl);
                    default -> help(repl);
                };
            } else {
                return switch (cmd) {
                    case "redraw" -> redraw(repl);
                    case "check" -> check();
                    case "leave" -> leave(repl);
                    case "help" -> help(repl);
                    default -> help(repl);
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private void redraw(Repl repl){
        if(!repl.getObserver()){
            if(repl.getPlayerColor().equals("white")){
                DrawBoard.main(ChessGame.TeamColor.WHITE);
            } else if (repl.getPlayerColor().equals("black")){
                DrawBoard.main(ChessGame.TeamColor.BLACK);
            }
        } else {
            DrawBoard.main(ChessGame.TeamColor.WHITE);
        }
    }

    public String help(Repl repl) {
        if (!repl.getObserver()) {
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
