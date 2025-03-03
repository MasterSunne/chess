package server;

import service.UserService;
import spark.*;

public class Server {
    private final AuthService aService;
    private final GameService gService;
    private final UserService uService;

    public Server(AuthService aService, GameService gService, UserService uService) {
        this.aService = aService;
        this.gService = gService;
        this.uService = uService;
    }


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
    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

}
