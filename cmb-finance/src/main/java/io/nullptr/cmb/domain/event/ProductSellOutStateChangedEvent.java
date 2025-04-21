package io.nullptr.cmb.domain.event;

import org.jmolecules.event.types.DomainEvent;

import java.time.Duration;

public record ProductSellOutStateChangedEvent(
        Long productId,
        String saleCode,
        String previousSellOutState,
        String currentSellOutSate,
        Duration changeDuration
) implements DomainEvent {

}
