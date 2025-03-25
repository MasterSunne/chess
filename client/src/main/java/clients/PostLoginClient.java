package clients;

import model.GameData;
import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostLoginClient {
    private final Repl repl;
    private final ServerFacade server;
    private ArrayList<String> gameList = new ArrayList<>();
    private Map<Integer, Integer> gameMap = new HashMap<>();

    public PostLoginClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(repl,params);
                case "create" -> createGame(repl, params);
                case "list" -> listGames(repl);
                case "join" -> joinGame(repl,params);
                case "observe" -> observeGame(repl,params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(Repl repl, String... params) throws ResponseException {
        LogoutRequest lr = new LogoutRequest(params[0]);
        server.logoutUser(lr);
        repl.setState(State.LOGGED_OUT);
        return "Successfully logged out";
    }

    private String createGame(Repl repl, String... params) throws ResponseException {
        CreateGameRequest cgr = new CreateGameRequest(params[0]);//pass in game name
        CreateGameResult result = server.createGame(repl.getAuthToken(),cgr);
        return String.format("Created game: " + params[0]);
    }

    private String listGames(Repl repl) throws ResponseException {
        ListGamesRequest lgr = new ListGamesRequest(repl.getAuthToken());
        ListGamesResult result = server.listGames(lgr);
        //need to make a new list of games and remember the numbering given to the user and return as string
        //displays the games in a numbered list, including the game name and players (not observers) in the game
        setGameList(result.games());

        StringBuilder sb = new StringBuilder();
        for (String str : gameList) {
            sb.append(str);
        }
        // Remove the trailing space
        String stringResult = sb.toString().trim();

        return stringResult;
    }

    private void setGameList(ArrayList<GameData> gameDataList){
        int i = 1;
        gameList.clear();
        for (GameData gData : gameDataList){
            gameMap.put(i, gData.gameID());
            String gameString = String.format("Game " + i + ": " + gData.gameName()
                    + "\n  White Player: " + gData.whiteUsername()
                    + "\n  Black Player: " + gData.blackUsername() + "\n");
            gameList.add(gameString);
            i++;
        }
    }

    private String joinGame(Repl repl, String[] params) throws ResponseException {
        try {
            int db_gameID = 0;
            if (!gameMap.isEmpty()) {
                db_gameID = gameMap.get(Integer.valueOf(params[0]));
            } else{
                throw new ResponseException(400, "Error: no games to join");
            }
            JoinGameRequest jgr = new JoinGameRequest(repl.getAuthToken(),params[1],db_gameID);
            server.joinGame(jgr);
            repl.setState(State.IN_GAME);
            return "Successfully joined game";
        } catch (NumberFormatException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ResponseException e) {
            throw new ResponseException(400,"Error: invalid gameID");
        }
    }

    private String observeGame(Repl repl, String[] params) throws ResponseException {
        return null;
    }


    public String help() {
        return RESET_TEXT_COLOR + """
                create <name>  - a game
                list  - all games
                join <ID> [WHITE|BLACK]  - a game
                observe <ID>  - a game
                logout  - when you are done
                quit  - playing chess
                help  - for possible commands
                """;

    }
}
