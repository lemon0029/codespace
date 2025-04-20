package io.nullptr.cmb.domain.event;

import org.jmolecules.event.types.DomainEvent;

public record ProductCreatedEvent(
        String saleCode
) implements DomainEvent {
}
