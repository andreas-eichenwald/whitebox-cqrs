package io.dabrowa.whitebox.command.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountCreditedEvent(@TargetAggregateIdentifier String accountNumber, long creditValue) {
}
