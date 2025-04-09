package clients;

import request.LoginRequest;
import request.RegisterRequest;
import result.RegLogResult;
import server.ServerFacade;
import server.ResponseException;

import java.util.Arrays;

public class PreLoginClient {
    private ClientData clientData;
    private final ServerFacade server;

    public PreLoginClient(String serverUrl, ClientData cd) {
        server = new ServerFacade(serverUrl);
        this.clientData = cd;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(clientData,params);
                case "register" -> register(clientData,params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(ClientData clientData, String... params) throws ResponseException {
        try {
            if (params.length == 2) {
                LoginRequest lr = new LoginRequest(params[0], params[1]);
                RegLogResult result = server.loginUser(lr);
                clientData.setAuthToken(result.authToken());
                clientData.setState(State.LOGGED_IN);
                return String.format("Now logged in as %s.", result.username());
            }
        } catch (Exception e) {
            throw new ResponseException(400, "Error: user not registered");
        }
        throw new ResponseException(400, "Expected: <yourName>, <yourPassword>");
    }


    public String register(ClientData clientData, String... params) throws ResponseException {
        try {
            if (params.length == 3 ) {
                RegisterRequest rr = new RegisterRequest(params[0],params[1],params[2]);
                RegLogResult result = server.registerUser(rr);
                clientData.setAuthToken(result.authToken());
                clientData.setState(State.LOGGED_IN);
                return String.format("Now registered as %s.", result.username());
            }
        } catch (Exception e) {
            throw new ResponseException(400, e.getMessage());
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

