package io.dabrowa.whitebox.domain.aggregates;

import io.dabrowa.whitebox.domain.commands.CreateAccountCommand;
import io.dabrowa.whitebox.domain.events.AccountCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
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

    @CommandHandler
    public AccountAggregate(final CreateAccountCommand command) {
        LOGGER.info("Creating account {}", command.accountId());
        AggregateLifecycle.apply(new AccountCreatedEvent(command.accountId(), command.initialBalance(), command.overdraftLimit()));
    }

    @EventSourcingHandler
    public void handle(final AccountCreatedEvent event) {
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
