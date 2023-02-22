package io.dabrowa.whitebox.domain.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreditAccountCommand(@TargetAggregateIdentifier String accountNumber, long creditValue) {
}
