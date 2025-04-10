package clients;

import chess.ChessGame;
import model.GameData;
import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostLoginClient {
    private ClientData clientData;
    private Repl repl;
    private final ServerFacade server;
    private final ArrayList<String> gameList = new ArrayList<>();
    private final Map<Integer, Integer> gameMap = new HashMap<>();
    private final String url;

    public PostLoginClient(String serverUrl, ClientData cd, Repl repl) {
        url = serverUrl;
        server = new ServerFacade(serverUrl);
        this.clientData = cd;
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(clientData);
                case "create" -> createGame(clientData, params);
                case "list" -> listGames(clientData);
                case "join" -> joinGame(clientData,params);
                case "observe" -> observeGame(clientData,params);
                case "clear" -> clearDB(clientData,params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String clearDB(ClientData clientData,String[] params) throws ResponseException{
        if(params.length == 0){
            return "Invalid permissions";
        }
        if (params[0].equals("987")){
            logout(clientData);
            server.clearAll();
        } else{
            return "Invalid permissions";
        }
        return "";
    }

    public String logout(ClientData clientData) throws ResponseException {
        LogoutRequest lr = new LogoutRequest(clientData.getAuthToken());
        server.logoutUser(lr);
        clientData.setState(State.LOGGED_OUT);
        return "Successfully logged out";
    }

    private String createGame(ClientData clientData, String... params) throws ResponseException {
        try {
            CreateGameRequest cgr = new CreateGameRequest(params[0]);//pass in game name
            server.createGame(clientData.getAuthToken(),cgr);
            return String.format("Created game: " + params[0]);
        } catch (ResponseException e) {
            throw new RuntimeException("Game already exists");
        }
    }

    private String listGames(ClientData clientData) throws ResponseException {
        ListGamesRequest lgr = new ListGamesRequest(clientData.getAuthToken());
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
        gameMap.clear();
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

    private String joinGame(ClientData clientData, String[] params) throws ResponseException {
        try {
            String x = checkJoinInput(params);
            if (x != null) {return x;}
            int dbgameID = 0;
            if (!gameMap.isEmpty()) {
                dbgameID = gameMap.get(Integer.valueOf(params[0]));
                clientData.setGameID(dbgameID);
            } else{
                throw new ResponseException(400, "Error: no games to join");
            }
            if (params[1].equalsIgnoreCase("WHITE")) {
                clientData.setPlayerColor("white");
            } else if (params[1].equalsIgnoreCase("BLACK")){
                clientData.setPlayerColor("black");
            } else {
                throw new ResponseException(400, "teamColor not valid");
            }

            JoinGameRequest jgr = new JoinGameRequest(clientData.getAuthToken(),params[1].toUpperCase(), dbgameID);
            server.joinGame(jgr);
            WebSocketFacade ws = new WebSocketFacade(url,repl);
            clientData.setWsf(ws);
            clientData.getWsf().connect(clientData);
            clientData.setState(State.IN_GAME);
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

    private String observeGame(ClientData clientData, String[] params) throws ResponseException {
        String x = checkObserveInput(params);
        if (x != null) {return x;}
        int dbgameID = 0;
        if (!gameMap.isEmpty()) {
            dbgameID = gameMap.get(Integer.valueOf(params[0]));
            clientData.setGameID(dbgameID);
        } else{
            throw new ResponseException(400, "Error: no games to join");
        }
        clientData.setPlayerColor("white");
        clientData.setIsObserver(true);
        WebSocketFacade ws = new WebSocketFacade(url,repl);
        clientData.setWsf(ws);
        clientData.getWsf().connect(clientData);
        clientData.setState(State.IN_GAME);
        return "";
    }

    private String checkObserveInput(String[] params) {
        if (params.length < 1 ){
            return "Expected: observe <ID>";
        }
        try {
            if (gameMap.get(Integer.parseInt(params[0])) == null){
                return "Invalid ID try using \"list\" to refresh valid game IDs";
            }
            if (gameMap.isEmpty()){
                return "Please use \"list\" first to view available games and IDs";
            }
        } catch (NumberFormatException e) {
            return params[0] + " is not a valid gameID";
        }
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
