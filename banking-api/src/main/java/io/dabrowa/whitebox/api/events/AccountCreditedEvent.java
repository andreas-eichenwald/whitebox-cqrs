package io.dabrowa.whitebox.api.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record AccountCreditedEvent(@TargetAggregateIdentifier String accountNumber, BigDecimal creditValue, long creditedAtEpochMillis) {
}
