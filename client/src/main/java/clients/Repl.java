package clients;

import clients.*;
import ui.DrawBoard;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameplayClient gameplayClient;
    public String authToken;
    public State state = State.LOGGED_OUT;

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public String getAuthToken(){
        return this.authToken;
    }

    public void setState(State state) {
        this.state = state;
    }



    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl, this);
        postLoginClient = new PostLoginClient(serverUrl, this);
        gameplayClient = new GameplayClient(serverUrl, this);
        authToken = "";
    }

    public void run() {
        System.out.println(BLACK_QUEEN + " Welcome to Will Larsen's 240 Chess Program! " + BLACK_QUEEN);
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (state == State.LOGGED_OUT) {
                printPrompt(state);
                String line = scanner.nextLine();
                try {
                    result = preLoginClient.eval(line);
                    System.out.print(RESET_TEXT_COLOR + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            else if (state == State.LOGGED_IN) {
                System.out.print(SET_TEXT_COLOR_BLUE);
                printPrompt(state);
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
        }
        System.out.println();
    }

    private void printPrompt(State state) {
        System.out.print("\n[" + state + "]"+ RESET_TEXT_COLOR + " >>> " + SET_TEXT_COLOR_GREEN);
    }

}
