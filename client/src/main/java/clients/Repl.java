package clients;

import chess.ChessBoard;
import chess.ChessGame;
import clients.*;
import com.google.gson.Gson;
import server.ResponseException;
import ui.DrawBoard;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameplayClient gameplayClient;
    public ClientData clientData;

    public Repl(String serverUrl) throws ResponseException {
        clientData = new ClientData();
        preLoginClient = new PreLoginClient(serverUrl, clientData);
        postLoginClient = new PostLoginClient(serverUrl, clientData, this);
        gameplayClient = new GameplayClient(clientData);
    }

    public void run() {
        System.out.println("\n"+BLACK_QUEEN + " Welcome to Will Larsen's 240 Chess Program! " + BLACK_QUEEN+"\n");
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (clientData.getState() == State.LOGGED_OUT) {
                printPrompt(clientData.getState());
                String line = scanner.nextLine();
                try {
                    result = preLoginClient.eval(line);
                    System.out.print(RESET_TEXT_COLOR + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            else if (clientData.getState() == State.LOGGED_IN) {
                System.out.print(SET_TEXT_COLOR_BLUE);
                printPrompt(clientData.getState());
                System.out.print(RESET_TEXT_COLOR);
                String line = scanner.nextLine();
                try {
                    result = postLoginClient.eval(line);
                    System.out.print(RESET_TEXT_COLOR + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
             else if (clientData.getState() == State.IN_GAME){
                System.out.print(SET_TEXT_COLOR_GREEN);
                printPrompt(clientData.getState());
                System.out.print(RESET_TEXT_COLOR);
                String line = scanner.nextLine();
                try {
                    result = gameplayClient.eval(line);
                    System.out.print(RESET_TEXT_COLOR + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }


    private void printPrompt(State state) {
        System.out.print("\n[" + state + "]"+ RESET_TEXT_COLOR + " >>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(String message) {
        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){

//            System.out.println("received load game");
            LoadGameMessage lgm = new Gson().fromJson(message, LoadGameMessage.class);
            clientData.setGame(lgm.getGame());
            ChessBoard board = clientData.getGame().getBoard();
            DrawBoard.main(ChessGame.TeamColor.WHITE,board);

        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){

            NotificationMessage nm = new Gson().fromJson(message,NotificationMessage.class);
            System.out.println(SET_TEXT_COLOR_BLUE + nm.getMessage());

        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){

            ErrorMessage em = new Gson().fromJson(message,ErrorMessage.class);
            System.out.println(SET_TEXT_COLOR_BLUE + em.getErrorMessage());

        } else{
            throw new RuntimeException("Error: invalid ServerMessageType");
        }
        System.out.print(RESET_TEXT_COLOR);
        printPrompt(clientData.getState());
    }
}
