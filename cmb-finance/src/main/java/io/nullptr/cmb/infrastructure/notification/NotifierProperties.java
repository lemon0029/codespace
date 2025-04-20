package io.nullptr.cmb.infrastructure.notification;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "notifier")
public class NotifierProperties {

    private boolean enabled;
    private Set<NotifierChannel> channels;
    private String wecomChatBotWebhookUrl;
}
