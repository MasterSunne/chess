package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;

    public String getErrorMessage(){
        return errorMessage;
    }

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }
}
