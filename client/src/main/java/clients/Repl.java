package clients;

import clients.*;
import ui.DrawBoard;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameplayClient gameplayClient;
    public ClientData clientData;
//    public String authToken;
//    public State state = State.LOGGED_OUT;
//    public String playerColor;
//    public WebSocketFacade ws;
//    public Boolean isObserver;

//    public void setAuthToken(String authToken){
//        this.authToken = authToken;
//    }
//
//    public String getAuthToken(){
//        return this.authToken;
//    }
//
//    public void setState(State state) {
//        this.state = state;
//    }

//    public State getState(){return this.state;}
//
//    public void setPlayerColor(String pc){ this.playerColor = pc;}
//
//    public String getPlayerColor(){return this.playerColor;}
//
//    public void setObserver(Boolean observer) {isObserver = observer;}
//
//    public Boolean getObserver() {return isObserver;}
//
//    public void setWebSocketFacade(WebSocketFacade new_ws) {this.ws = new_ws;}
//
//    public WebSocketFacade getWebSocketFacade() {return this.ws;}


    public Repl(String serverUrl) {
        clientData = new ClientData();
        preLoginClient = new PreLoginClient(serverUrl, clientData);
        postLoginClient = new PostLoginClient(serverUrl, clientData, this);
        gameplayClient = new GameplayClient(serverUrl, clientData);
//        authToken = "";
//        playerColor = null;
//        isObserver = false;
//        ws = null;
    }

    public void run() {
        System.out.println(BLACK_QUEEN + " Welcome to Will Larsen's 240 Chess Program! " + BLACK_QUEEN);
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
    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_BLUE + notification.message());
        printPrompt(clientData.getState());
    }
}
