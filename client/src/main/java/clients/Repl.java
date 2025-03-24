package clients;

import clients.*;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameplayClient gameplayClient;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    private State state = State.LOGGED_OUT;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl, this);
        postLoginClient = new PostLoginClient(serverUrl, this);
        gameplayClient = new GameplayClient(serverUrl, this);
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

    private void printPrompt(State state) {
        System.out.print("\n[" + SET_TEXT_COLOR_BLACK + state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }

}
