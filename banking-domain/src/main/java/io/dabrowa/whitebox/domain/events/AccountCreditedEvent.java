package io.dabrowa.whitebox.domain.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountCreditedEvent(@TargetAggregateIdentifier String accountNumber, long creditValue) {
}
