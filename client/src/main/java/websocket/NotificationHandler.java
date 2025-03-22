package java.websocket;

import webSocketMessages.Notification;

import java.State;

public interface NotificationHandler {
    void notify(Notification notification, State state);
}
