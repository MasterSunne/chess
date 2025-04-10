package clients;

import chess.*;
import server.ResponseException;
import ui.DrawBoard;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayClient {
//    private final WebSocketFacade wsf;
    private ClientData clientData;

    public GameplayClient(ClientData cd) throws ResponseException {
//        wsf = cd.getWsf();
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
                    case "check" -> check(clientData, params);
                    case "resign" -> resign(clientData);
                    case "leave" -> leave(clientData);
                    case "help" -> help(clientData);
                    default -> help(clientData);
                };
            } else {
                return switch (cmd) {
                    case "redraw" -> redraw(clientData);
                    case "check" -> check(clientData, params);
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
        try {
            if (!clientData.getGame().getGameOver()) {
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
                clientData.getWsf().makeMove(clientData,chessMove);
                return "";
            } else {
                throw new ResponseException(407,"Error: the game has ended");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private String redraw(ClientData clientData){
        ChessBoard board = clientData.getGame().getBoard();

        if(!clientData.getIsObserver()){
            if(clientData.getPlayerColor().equals("white")){
                DrawBoard.main(clientData.getPlayerColor(),clientData.getGame());
            } else if (clientData.getPlayerColor().equals("black")){
                DrawBoard.main(clientData.getPlayerColor(),clientData.getGame());
            }
        } else {
            DrawBoard.main(clientData.getPlayerColor(),clientData.getGame());
        }
        return "";
    }

    private String check(ClientData clientData, String[] params){
        ChessGame game = clientData.getGame();
        ChessGameValidMoves validMovesObj = new ChessGameValidMoves(game);

        String startString = params[0];
        char startChar = startString.charAt(0);
        int startColumnValue = Character.getNumericValue(startChar) - Character.getNumericValue('a') + 1;
        ChessPosition startPos = new ChessPosition(startString.charAt(1), startColumnValue);
        Collection<ChessMove> validMovesList = validMovesObj.validMoves(startPos);
        StringBuilder positionsOutput = new StringBuilder("Valid Moves: ");
        for (ChessMove move : validMovesList) {
            positionsOutput.append(move.toString()).append(", ");
        }

        // Remove the trailing comma and space if the list is not empty
        if (!validMovesList.isEmpty()) {
            positionsOutput.setLength(positionsOutput.length() - 2);
        }
        return positionsOutput.toString();
    }

    private String leave(ClientData clientData){
        try {
            clientData.getWsf().leave(clientData);
            clientData.setWsf(null);
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
                    clientData.getGame().setGameOver(true);
                    clientData.getWsf().resign(clientData);
                    clientData.setWsf(null);
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
