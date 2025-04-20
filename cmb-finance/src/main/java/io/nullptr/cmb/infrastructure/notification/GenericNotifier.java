package io.nullptr.cmb.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GenericNotifier {

    public void notify(Notification notification) {
        try {
            doNotify(notification);
        } catch (Exception e) {
            log.warn("Failed to send notification with {}: {}", this.getClass().getName(), notification.asString());
        }
    }

    public abstract NotifierChannel channel();

    protected abstract void doNotify(Notification notification) throws Exception;
}
