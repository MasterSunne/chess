package java.websocket;

import webSocketMessages.Notification;

import clients.State;

public interface NotificationHandler {
    void notify(Notification notification, State state);
}
