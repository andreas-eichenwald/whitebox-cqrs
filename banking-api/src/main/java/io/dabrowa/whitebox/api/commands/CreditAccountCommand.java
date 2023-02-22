package io.dabrowa.whitebox.api.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record CreditAccountCommand(@TargetAggregateIdentifier String accountNumber, BigDecimal creditValue) {
}
