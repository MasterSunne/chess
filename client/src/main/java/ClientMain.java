
import chess.*;
import clients.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:3306";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}