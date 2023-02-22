package io.dabrowa.whitebox.domain.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateAccountCommand(@TargetAggregateIdentifier String accountId, long initialBalance, long overdraftLimit) {
}
