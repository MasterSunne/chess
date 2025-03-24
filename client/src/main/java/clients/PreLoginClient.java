package java.clients;

import clients.Repl;
import clients.State;
import com.google.gson.Gson;

import java.util.Arrays;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            repl.state = State.LOGGED_IN;
            visitorName = String.join("-", params);
            server.login(params[0],params[1]);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <yourname>, <yourpassword>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            repl.state = State.LOGGED_IN;
            visitorName = String.join("-", params);
            server.login(params[0],params[1],params[2]);
            return String.format("You registered as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <yourname>, <yourpassword>, <youremail>");
    }


    public String help() {
        return """
                - register <yourname>, <password>, <email>
                - login <yourname>
                - quit
                """;

    }
}
