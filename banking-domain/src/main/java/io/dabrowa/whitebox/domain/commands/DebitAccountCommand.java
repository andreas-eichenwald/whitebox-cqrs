package io.dabrowa.whitebox.domain.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record DebitAccountCommand(@TargetAggregateIdentifier String accountNumber, long debitValue) {
}
