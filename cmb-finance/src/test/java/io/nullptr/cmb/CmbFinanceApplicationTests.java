package io.nullptr.cmb;

import io.nullptr.cmb.domain.event.ProductCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
class CmbFinanceApplicationTests {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void contextLoads() {
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent("123");
        applicationEventPublisher.publishEvent(productCreatedEvent);
    }

}
