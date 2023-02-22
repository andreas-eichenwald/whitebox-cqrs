package io.dabrowa.whitebox.domain.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountDebitedEvent(@TargetAggregateIdentifier String accountNumber, long debitValue) {
}
