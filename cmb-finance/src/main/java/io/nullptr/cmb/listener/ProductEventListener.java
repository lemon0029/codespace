package io.nullptr.cmb.listener;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.event.ProductCreatedEvent;
import io.nullptr.cmb.domain.event.ProductSaleOutStateChangedEvent;
import io.nullptr.cmb.domain.repository.ProductRepository;
import io.nullptr.cmb.infrastructure.notification.MessageType;
import io.nullptr.cmb.infrastructure.notification.Notification;
import io.nullptr.cmb.infrastructure.notification.Notifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final Notifier notifier;
    private final ProductRepository productRepository;

    @TransactionalEventListener(ProductCreatedEvent.class)
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event);
        // TODO 推送企业微信通知
    }

    @TransactionalEventListener(ProductSaleOutStateChangedEvent.class)
    public void onProductSaleOutStateChanged(ProductSaleOutStateChangedEvent event) {
        log.info("Received product sale-out state changed event: {}", event);

        Product product = productRepository.findByInnerCode(event.saleCode())
                .orElseThrow();

        notifier.notify(new Notification() {

            @Override
            public MessageType messageType() {
                return MessageType.MARKDOWN;
            }

            @Override
            public String messageContent() {
                return "Product Sell-Out State Changed\n" +
                        "> Code: " + product.getSaleCode() + "\n" +
                        "> Name: " + product.getShortName() + "\n" +
                        "> Sell-Out State: %s -> %s".formatted(event.previousSellOutState(), event.currentSellOutSate());
            }
        });
    }
}
