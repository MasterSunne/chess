package clients;

import chess.ChessGame;
import model.GameData;
import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;

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
                case "logout" -> logout(repl);
                case "create" -> createGame(repl, params);
                case "list" -> listGames(repl);
                case "join" -> joinGame(repl,params);
                case "observe" -> observeGame(repl,params);
                case "clear" -> clearDB(repl,params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String clearDB(Repl repl,String[] params) throws ResponseException{
        if(params.length == 0){
            return "Invalid permissions";
        }
        if (params[0].equals("987")){
            logout(repl);
            server.clearAll();
        } else{
            return "Invalid permissions";
        }
        return "";
    }

    public String logout(Repl repl) throws ResponseException {
        LogoutRequest lr = new LogoutRequest(repl.getAuthToken());
        server.logoutUser(lr);
        repl.setState(State.LOGGED_OUT);
        return "Successfully logged out";
    }

    private String createGame(Repl repl, String... params) throws ResponseException {
        CreateGameRequest cgr = new CreateGameRequest(params[0]);//pass in game name
        server.createGame(repl.getAuthToken(),cgr);
        return String.format("Created game: " + params[0]);
    }

    private String listGames(Repl repl) throws ResponseException {
        ListGamesRequest lgr = new ListGamesRequest(repl.getAuthToken());
        ListGamesResult result = server.listGames(lgr);
        setGameList(result.games());

        StringBuilder sb = new StringBuilder();
        for (String str : gameList) {
            sb.append(str);
        }
        // Remove the trailing space
        String stringResult = sb.toString().trim();
        if (stringResult.equals("")){
            stringResult = "No games exist yet! Use \"create\" to make a new game.";
        }
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
            String x = checkJoinInput(params);
            if (x != null) {
                return x;
            }
            int dbgameID = 0;
            if (!gameMap.isEmpty()) {
                dbgameID = gameMap.get(Integer.valueOf(params[0]));
            } else{
                throw new ResponseException(400, "Error: no games to join");
            }
            JoinGameRequest jgr = new JoinGameRequest(repl.getAuthToken(),params[1].toUpperCase(), dbgameID);
            server.joinGame(jgr);
//            repl.setState(State.IN_GAME);
            if (params[1].equalsIgnoreCase("WHITE")) {
                DrawBoard.main(ChessGame.TeamColor.WHITE);
            } else if (params[1].equalsIgnoreCase("BLACK")){
                DrawBoard.main(ChessGame.TeamColor.BLACK);
            } else {
                throw new ResponseException(400, "teamColor not valid");
            }
            return "Successfully joined game";
        } catch (NumberFormatException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ResponseException e) {
            throw new ResponseException(400,e.getMessage());
        }
    }

    private String checkJoinInput(String[] params) {
        if (params.length < 2 ){
            return "Expected: join <ID> [WHITE|BLACK]";
        }
        try {
            int i = Integer.parseInt(params[0]);
            if (gameMap.isEmpty()){
                return "Please use \"list\" first to view available games and IDs";
            }
            if (gameMap.get(Integer.parseInt(params[0])) == null){
                return "Invalid ID try using \"list\" to refresh valid game IDs";
            }
        } catch (NumberFormatException e) {
            return params[0] + " is not a valid gameID";
        }
        if (params[1] == null) {
            return "Expected: join <ID> [WHITE|BLACK]";
        } else if (!params[1].equals("white") && !params[1].equals("black")){
            return "Expected: join <ID> [WHITE|BLACK]";
        }
        return null;
    }

    private String observeGame(Repl repl, String[] params) throws ResponseException {
        DrawBoard.main(ChessGame.TeamColor.WHITE);
        return "";
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
