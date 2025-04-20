package io.nullptr.cmb.infrastructure.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeComNotifier extends GenericNotifier {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final NotifierProperties notifierProperties;

    @Override
    protected void doNotify(Notification notification) throws Exception {
        MessageType messageType = notification.messageType();
        String messageContent = notification.messageContent();

        if (Objects.requireNonNull(messageType) == MessageType.MARKDOWN) {
            Map<String, Object> requestBody = Map.of("msgtype", "markdown",
                    "markdown", Map.of("content", messageContent));

            sendNotification(requestBody);
        }
    }

    private void sendNotification(Map<String, Object> requestBody) throws JsonProcessingException {
        String webhookUrl = notifierProperties.getWecomChatBotWebhookUrl();

        String requestBodyAsString = objectMapper.writeValueAsString(requestBody);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                webhookUrl, requestBodyAsString.getBytes(StandardCharsets.UTF_8), String.class);

        log.info("Received response: {}", responseEntity);
    }

    @Override
    public NotifierChannel channel() {
        return NotifierChannel.WECOM;
    }
}
