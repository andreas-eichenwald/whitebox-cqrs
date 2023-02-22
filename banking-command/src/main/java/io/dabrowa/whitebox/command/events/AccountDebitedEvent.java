package io.dabrowa.whitebox.command.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AccountDebitedEvent(@TargetAggregateIdentifier String accountNumber, long debitValue) {
}
