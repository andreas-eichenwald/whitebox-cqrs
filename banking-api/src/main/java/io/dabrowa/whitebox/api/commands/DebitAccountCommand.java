package io.dabrowa.whitebox.api.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record DebitAccountCommand(@TargetAggregateIdentifier String accountNumber, BigDecimal debitValue) {
}
