package io.nullptr.cmb.listener;

import io.nullptr.cmb.domain.event.ProductCreatedEvent;
import io.nullptr.cmb.domain.event.ProductSaleOutStateChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ProductEventListener {

    @TransactionalEventListener(ProductCreatedEvent.class)
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event);
        // TODO 推送企业微信通知
    }

    @TransactionalEventListener(ProductSaleOutStateChangedEvent.class)
    public void onProductSaleOutStateChanged(ProductSaleOutStateChangedEvent event) {
        log.info("Received product sale-out state changed event: {}", event);
        // TODO 推送企业微信通知
    }
}
