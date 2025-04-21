package io.nullptr.cmb.appliation.listener;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductZsTag;
import io.nullptr.cmb.domain.event.ProductCreatedEvent;
import io.nullptr.cmb.domain.event.ProductSaleOutStateChangedEvent;
import io.nullptr.cmb.domain.repository.ProductRepository;
import io.nullptr.cmb.infrastructure.common.Constants;
import io.nullptr.cmb.infrastructure.notification.MessageType;
import io.nullptr.cmb.infrastructure.notification.Notification;
import io.nullptr.cmb.infrastructure.notification.Notifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final Notifier notifier;
    private final ProductRepository productRepository;

    private static final String BASE_URL = "https://mobile.cmbchina.com/IEntrustFinance/subsidiaryproduct/financedetail.html?XRIPINN=%s&XSAACOD=D07";

    @TransactionalEventListener(ProductCreatedEvent.class)
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event);

        Product product = productRepository.findByInnerCode(event.saleCode())
                .orElseThrow();

        notifier.notify(new Notification() {

            @Override
            public MessageType messageType() {
                return MessageType.MARKDOWN;
            }

            @Override
            public String messageContent() {
                String url = BASE_URL.formatted(product.getSaleCode());

                ProductZsTag productZsTag = ProductZsTag.fromCode(product.getProductTag());

                StringBuilder builder = new StringBuilder();
                builder.append("New product on sale\n")
                        .append("> Code: ").append(product.getSaleCode()).append("\n");

                if (productZsTag != null) {
                    builder.append("> Type: ").append(productZsTag.getName()).append("\n");
                }

                builder.append("> Name: ").append(product.getShortName()).append("\n")
                        .append("\n[View product detail](%s)".formatted(url));

                return builder.toString();
            }
        });
    }

    @TransactionalEventListener(ProductSaleOutStateChangedEvent.class)
    public void onProductSaleOutStateChanged(ProductSaleOutStateChangedEvent event) {
        log.info("Received product sale-out state changed event: {}", event);

        if (event.previousSellOutState() == null) {
            return;
        }

        Product product = productRepository.findByInnerCode(event.saleCode())
                .orElseThrow();

        notifier.notify(new Notification() {

            @Override
            public MessageType messageType() {
                return MessageType.MARKDOWN;
            }

            @Override
            public String messageContent() {
                String url = BASE_URL.formatted(product.getSaleCode());
                String previousSellOutSate = event.previousSellOutState();
                String currentSellOutSate = event.currentSellOutSate();

                ProductZsTag productZsTag = ProductZsTag.fromCode(product.getProductTag());

                // 当前是否为售罄状态
                boolean currentSellOut = Constants.PRODUCT_SALE_OUT_N.equals(previousSellOutSate) &&
                        Constants.PRODUCT_SALE_OUT_Y.equals(currentSellOutSate);

                Duration duration = event.changeDuration();

                StringBuilder builder = new StringBuilder();
                builder.append("New product on sale\n")
                        .append("> Code: ").append(product.getSaleCode()).append("\n");

                if (productZsTag != null) {
                    builder.append("> Type: ").append(productZsTag.getName()).append("\n");
                }

                builder.append("> Name: ").append(product.getShortName()).append("\n")
                        .append("> Sell-Out State: ").append(currentSellOutSate).append("\n");

                if (currentSellOut) {
                    builder.append("> Duration: ")
                            .append(duration)
                            .append("\n");
                }

                builder.append("\n[View product detail](%s)".formatted(url));

                return builder.toString();
            }
        });
    }
}
