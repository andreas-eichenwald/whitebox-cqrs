package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountCreatedEvent(@TargetAggregateIdentifier String accountId,
                                  long initialBalance,
                                  long overdraftLimit) implements Event {
}
