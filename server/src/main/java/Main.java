import chess.*;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);

            String dataAccess = "MEMORY";
            var aService = new AuthService(dataAccess);
            var gService = new GameService(dataAccess);
            var uService = new UserService(dataAccess);
            var server = new Server(aService,gService,uService);
            server.run(port);
            System.out.printf("Server started on port %d with %s%n", port, dataAccess);
            return;
        } catch (Throwable e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }
}