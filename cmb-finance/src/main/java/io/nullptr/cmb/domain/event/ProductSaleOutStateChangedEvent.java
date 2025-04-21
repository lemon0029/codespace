package io.nullptr.cmb.domain.event;

import org.jmolecules.event.types.DomainEvent;

import java.time.Duration;

public record ProductSaleOutStateChangedEvent(
        Long productId,
        String saleCode,
        String previousSellOutState,
        String currentSellOutSate,
        Duration changeDuration
) implements DomainEvent {

}
