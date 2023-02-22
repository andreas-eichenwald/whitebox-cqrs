package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountCreditedEvent(@TargetAggregateIdentifier String accountNumber, long creditValue, long creditedAtEpochMillis) {
}
