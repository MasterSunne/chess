import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            System.out.println("♕ 240 Chess Server ♕" );

            String dataAccess = "SQL";
            var server = new Server();
            server.run(port);
            System.out.printf("Http server started on port %d with %s%n", port, dataAccess);
        } catch (Throwable e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }
}