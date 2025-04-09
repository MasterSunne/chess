package clients;

import chess.ChessBoard;
import websocket.WebSocketFacade;

public class ClientData {
    private WebSocketFacade wsf;
    private String authToken;
    private State state;
    private String playerColor;
    private Boolean isObserver;
    private ChessBoard gameBoard;

    // Constructor
    public ClientData() {
        this.wsf = null;
        this.authToken = "";
        this.playerColor = "";
        this.isObserver = false;
        this.state = State.LOGGED_OUT;
        this.gameBoard = null;
    }

    // Getters and Setters
    public WebSocketFacade getWsf() {
        return wsf;
    }

    public void setWsf(WebSocketFacade wsf) {
        this.wsf = wsf;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public Boolean getIsObserver() {
        return isObserver;
    }

    public void setIsObserver(Boolean isObserver) {
        this.isObserver = isObserver;
    }

    public ChessBoard getGameBoard(){
        return gameBoard;
    }

    public void setGameBoard(ChessBoard gb){
        this.gameBoard = gb;
    }

}
