package io.dabrowa.whitebox.domain.aggregates.account;

import io.dabrowa.whitebox.domain.events.AccountCreatedEvent;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class AccountAggregate {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAggregate.class);

    @AggregateIdentifier
    private String number;

    private long initialBalance;

    private long overdraftLimit;

    AccountAggregate() {
    }

    @EventSourcingHandler
    public void handle(final AccountCreatedEvent event) {
        LOGGER.debug("Handling event: {}", event);
        this.number = event.accountId();
        this.overdraftLimit = event.overdraftLimit();
        this.initialBalance = event.initialBalance();
    }

    public String getNumber() {
        return number;
    }

    public long getInitialBalance() {
        return initialBalance;
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }
}
