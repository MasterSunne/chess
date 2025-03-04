package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import service.GameService;
import service.ServiceException;
import service.UserService;
import spark.*;
import Request.*;
import Result.*;

public class Server {

    AuthDAO aDAO = new MemoryAuthDAO();
    GameDAO gDAO = new MemoryGameDAO();
    UserDAO uDAO = new MemoryUserDAO();
    GameService gService = new GameService(aDAO,gDAO,uDAO);
    UserService uService = new UserService(aDAO,uDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

//        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session",this::loginUser);
//        Spark.post("/game",this::createGame);
//        Spark.put("/game",this::joinGame);
//        Spark.get("/game", this::listGames);
        Spark.delete("/session", this::logoutUser);
//        Spark.delete("/db", this::clearAll);
        Spark.exception(DataAccessException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) throws RequestException, DataAccessException {
        try {
            var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegLogResult rResult = uService.register(registerRequest);
            return new Gson().toJson(rResult);

        } catch (JsonSyntaxException e) {
            throw new RequestException(400,"Error: bad request");
        } catch (ServiceException e) {
            throw new DataAccessException(401,e.getMessage());
        }
    }

    private Object loginUser(Request req, Response res) throws RequestException, DataAccessException {
        try {
            var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            RegLogResult rResult = uService.login(loginRequest);
            return new Gson().toJson(rResult);

        } catch (JsonSyntaxException e) {
            throw new RequestException(400,"Error: bad request");
        }
    }

    private Object logoutUser(Request req, Response res) throws RequestException, DataAccessException {
        try {
            String token = req.headers("Authorization");
            if (token != null) {
                LogoutRequest request = new LogoutRequest(token);
                uService.logout(request);
                res.status(200);
            }
        } catch (JsonSyntaxException e) {
            throw new RequestException(400,"Error: bad request");
        }
        return null;
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

}
