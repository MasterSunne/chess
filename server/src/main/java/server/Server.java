package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import dataaccess.DataAccessException;
import service.UserService;
import spark.*;
import request.*;
import result.*;

public class Server {

    AuthDAO aDAO;

    {
        try {
            aDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    GameDAO gDAO = new SQLGameDAO();
    UserDAO uDAO = new SQLUserDAO();
    GameService gService = new GameService(aDAO,gDAO);
    UserService uService = new UserService(aDAO,uDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

//        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session",this::loginUser);
        Spark.post("/game",this::createGame);
        Spark.put("/game",this::joinGame);
        Spark.get("/game", this::listGames);
        Spark.delete("/session", this::logoutUser);
        Spark.delete("/db", this::clearAll);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    // all http handlers listed belows:
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearAll(Request req, Response res) throws DataAccessException {
        try {
            aDAO.clear();
            gDAO.clear();
            uDAO.clear();
            res.status(200);
            return "";
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(),e.getMessage());
        }
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        try {
            String token = req.headers("Authorization");
            if (token != null) {
                var createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
                CreateGameResult result = gService.createGame(token,createGameRequest);
                return new Gson().toJson(result);
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
        return "";
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        try {
            String token = req.headers("Authorization");
            if (token != null) {
                var joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
                if (joinGameRequest.playerColor() == null ||
                        joinGameRequest.playerColor().isEmpty() ||
                        !isValidTeamColor(joinGameRequest.playerColor())) {
                    throw new DataAccessException(400,"Error: bad request");
                }
                gService.joinGame(token,joinGameRequest);
                res.status(200);
                return "{}";
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
        return "";
    }

    private boolean isValidTeamColor(String s) {
        return s.equals("WHITE") || s.equals("BLACK");
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        try {
            String token = req.headers("Authorization");
            if (token != null) {
                ListGamesRequest request = new ListGamesRequest(token);
                ListGamesResult result = gService.listGames(request);
                return new Gson().toJson(result);
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
        return "";
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        try {
            var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegLogResult rResult = uService.register(registerRequest);
            return new Gson().toJson(rResult);

        } catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(),e.getMessage());
        }
    }

    private Object loginUser(Request req, Response res) throws DataAccessException {
        try {
            var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            RegLogResult rResult = uService.login(loginRequest);
            return new Gson().toJson(rResult);
        } catch (DataAccessException e){
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
    }

    private Object logoutUser(Request req, Response res) throws DataAccessException {
        try {
            String token = req.headers("Authorization");
            if (token != null) {
                LogoutRequest request = new LogoutRequest(token);
                uService.logout(request);
                res.status(200);
            }
        }  catch (DataAccessException e) {
            throw new DataAccessException(e.statusCode(), e.getMessage());
        }
        return "";
    }

    // handles any exception thrown by the web API and turns it into JSON
    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

}
