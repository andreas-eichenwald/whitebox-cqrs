package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record AccountDebitedEvent(@TargetAggregateIdentifier String accountNumber, BigDecimal debitValue, long creditedAtEpochMillis) {
}
