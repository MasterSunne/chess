package clients;

import java.websocket.NotificationHandler;
import webSocketMessages.Notification;

import java.clients.PostLoginClient;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final java.clients.PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private State state = State.LOGGED_OUT;

    public Repl(String serverUrl) {
        preLoginClient = new java.clients.PreLoginClient(serverUrl, this);
        postLoginClient = new PostLoginClient(serverUrl, this);
    }

    public void run() {
        System.out.println(BLACK_QUEEN + " Welcome to Will Larsen's 240 Chess Program! Type help to get started. " + BLACK_QUEEN);
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt(state);
            String line = scanner.nextLine();

            if (state == State.LOGGED_OUT) {
                try {
                    result = preLoginClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            else if (state == State.LOGGED_IN) {


                try {
                    result = postLoginClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }

    public void notify(Notification notification,State state) {
        System.out.println(SET_TEXT_COLOR_RED + notification.message());
        printPrompt(state);
    }

    private void printPrompt(State state) {
        System.out.print("\n[" + SET_TEXT_COLOR_BLACK + state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }

}
