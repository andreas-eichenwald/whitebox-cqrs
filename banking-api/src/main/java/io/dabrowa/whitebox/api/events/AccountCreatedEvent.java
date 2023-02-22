package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record AccountCreatedEvent(@TargetAggregateIdentifier String accountId,
                                  BigDecimal initialBalance,
                                  BigDecimal overdraftLimit) implements Event {
}
