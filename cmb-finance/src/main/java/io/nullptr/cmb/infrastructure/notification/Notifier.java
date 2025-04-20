package io.nullptr.cmb.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Notifier {

    private final NotifierProperties notifierProperties;
    private final List<GenericNotifier> genericNotifiers;

    public void notify(Notification notification) {
        if (!notifierProperties.isEnabled()) {
            return;
        }

        for (GenericNotifier notifier : genericNotifiers) {

            if (!notifierProperties.getChannels().contains(notifier.channel())) {
                continue;
            }

            notifier.notify(notification);
        }
    }
}
