package io.nullptr.cmb.domain.event;

import org.jmolecules.event.types.DomainEvent;

public record ProductSaleOutStateChangedEvent(
        Long productId,
        String saleCode,
        String previousSellOutState,
        String currentSellOutSate
) implements DomainEvent {

}
