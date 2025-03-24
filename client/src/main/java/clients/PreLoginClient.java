package clients;

import clients.Repl;
import clients.State;
import com.google.gson.Gson;
import model.AuthData;
import request.LoginRequest;
import request.RegisterRequest;
import result.RegLogResult;
import server.ServerFacade;
import server.ResponseException;

import java.util.Arrays;

public class PreLoginClient {
    private final Repl repl;
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(repl,params);
                case "register" -> register(repl,params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(Repl repl, String... params) throws ResponseException {
        if (params.length >= 1) {
            repl.setState(State.LOGGED_IN);
            visitorName = String.join("-", params);
            LoginRequest lr = new LoginRequest(params[0], params[1]);
            RegLogResult result = server.loginUser(lr);
            return String.format("You signed in as %s.", result.username());
        }
        throw new ResponseException(400, "Expected: <yourName>, <yourPassword>");
    }

    public String register(Repl repl, String... params) throws ResponseException {
        if (params.length >= 1) {
            repl.setState(State.LOGGED_IN);
            visitorName = String.join("-", params);
            RegisterRequest rr = new RegisterRequest(params[0],params[1],params[2]);
            RegLogResult result = server.registerUser(rr);
            return String.format("You registered as %s.", result.username());
        }
        throw new ResponseException(400, "Expected: <yourname>, <yourpassword>, <youremail>");
    }


    public String help() {
        return """
                register <your_name>, <password>, <email>  - to create an account
                login <your_name>  - to play chess
                quit  - playing chess
                help  - for possible commands
                """;

    }
}
