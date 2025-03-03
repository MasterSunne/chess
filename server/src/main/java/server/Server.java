package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final GameService gService = new GameService(dataAccess);
    private final UserService uService = new UserService(dataAccess);
    private final AuthDAO aDAO;
    private final GameDAO gDAO;
    private final UserDAO uDAO;


    public run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

//        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session",this::loginUser);
        Spark.post("/game",this::createGame);
        Spark.put("/game",this::joinGame);
        Spark.get("/game", this::listGames);
        Spark.delete("/session", this::logoutUser);
        Spark.delete("/db", this::clearAll);
        Spark.exception(ResponseException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return this;
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

//    private Object registerUser(Request req, Response res) throws ResponseException {
//        var pet = new Gson().fromJson(req.body(), RegisterRequest.class);
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
//    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

}
