package io.nullptr.cmb.infrastructure.notification;

public interface Notification {

    MessageType messageType();

    String messageContent();

    default String asString() {
        return "messageType: %s, messageContent: %s".formatted(messageType(), messageContent());
    }
}
