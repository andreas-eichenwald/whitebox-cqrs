package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountDebitedEvent(@TargetAggregateIdentifier String accountNumber, long debitValue) {
}
