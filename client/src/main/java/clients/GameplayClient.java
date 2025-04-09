package clients;

import chess.*;
import server.ResponseException;
import ui.DrawBoard;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayClient {
    private final WebSocketFacade wsf;
    private ClientData clientData;

    public GameplayClient(ClientData cd) throws ResponseException {
        wsf = cd.getWsf();
        this.clientData = cd;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!clientData.getIsObserver()) {
                return switch (cmd) {
                    case "move" -> move(clientData, params);
                    case "redraw" -> redraw(clientData);
//                    case "check" -> check();
                    case "resign" -> resign(clientData);
                    case "leave" -> leave(clientData);
                    case "help" -> help(clientData);
                    default -> help(clientData);
                };
            } else {
                return switch (cmd) {
                    case "redraw" -> redraw(clientData);
//                    case "check" -> check();
                    case "leave" -> leave(clientData);
                    case "help" -> help(clientData);
                    default -> help(clientData);
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String move(ClientData clientData, String[] params) throws ResponseException {
        String startString = params[0];
        char startChar = startString.charAt(0);
        int startColumnValue = Character.getNumericValue(startChar) - Character.getNumericValue('a') + 1;
        ChessPosition startPos = new ChessPosition(startString.charAt(1), startColumnValue);

        String endString = params[1];
        char endChar = endString.charAt(0);
        int endColumnValue = Character.getNumericValue(endChar) - Character.getNumericValue('a') + 1;
        ChessPosition endPos = new ChessPosition(endString.charAt(1), endColumnValue);

        ChessPiece.PieceType promotionPiece = null;
        if (params.length > 2){
            promotionPiece = ChessPiece.PieceType.valueOf(params[2]);
        }
        ChessMove chessMove = new ChessMove(startPos, endPos, promotionPiece);
        wsf.makeMove(clientData,chessMove);
        return "";
    }

    private String leave(ClientData clientData){
        try {
            wsf.leave(clientData);
            clientData.setState(State.LOGGED_IN);
            return "Successfully left game";
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private String resign(ClientData clientData){
        try {
            Scanner scanner = new Scanner(System.in);
            String line;
            String exitMsg;

            while (true) {
                System.out.println("Are you sure you want to resign? <YES|NO>");
                line = scanner.nextLine().trim().toLowerCase();

                if (line.equals("yes")) {
                    wsf.resign(clientData);
                    exitMsg = "Resignation successful";
                    break;
                } else if (line.equals("no")) {
                    exitMsg = "Resignation canceled";
                    break;
                } else {
                    // Handle invalid input
                    System.out.println("Invalid input: options are \"yes\" or \"no\". Please try again.");
                }
            }
            scanner.close();
            return exitMsg;
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private String redraw(ClientData clientData){
        ChessBoard board = clientData.getGame().getBoard();

        if(!clientData.getIsObserver()){
            if(clientData.getPlayerColor().equals("white")){
                DrawBoard.main(ChessGame.TeamColor.WHITE,board);
            } else if (clientData.getPlayerColor().equals("black")){
                DrawBoard.main(ChessGame.TeamColor.BLACK,board);
            }
        } else {
            DrawBoard.main(ChessGame.TeamColor.WHITE,board);
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
