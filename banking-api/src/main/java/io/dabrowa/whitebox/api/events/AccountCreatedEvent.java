package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record AccountCreatedEvent(@TargetAggregateIdentifier String accountNumber,
                                  BigDecimal initialBalance,
                                  BigDecimal overdraftLimit) implements Event {
}
