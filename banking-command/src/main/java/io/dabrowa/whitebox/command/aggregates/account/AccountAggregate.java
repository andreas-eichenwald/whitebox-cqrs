package io.dabrowa.whitebox.command.aggregates.account;

import io.dabrowa.whitebox.command.events.AccountCreatedEvent;
import io.dabrowa.whitebox.command.events.AccountDebitedEvent;
import io.dabrowa.whitebox.command.commands.CreditAccountCommand;
import io.dabrowa.whitebox.command.commands.DebitAccountCommand;
import io.dabrowa.whitebox.command.events.AccountCreditedEvent;
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

    public static class InsufficientFundsException extends Exception {
        // TODO: Constructor with details
    }

    @AggregateIdentifier
    private String number;

    private long overdraftLimit;

    private long balance;

    AccountAggregate() {
    }

    @EventSourcingHandler
    public void handle(final AccountCreatedEvent event) {
        LOGGER.debug("Handling event: {}", event);
        this.number = event.accountId();
        this.overdraftLimit = event.overdraftLimit();
        this.balance = event.initialBalance();
    }

    @CommandHandler
    public void handle(final CreditAccountCommand command) {
        AggregateLifecycle.apply(new AccountCreditedEvent(this.number, command.creditValue()));
    }

    @CommandHandler
    public void handle(final DebitAccountCommand command) throws InsufficientFundsException {
        if (this.balance - command.debitValue() < -this.overdraftLimit) {
            throw new InsufficientFundsException();
        }
        AggregateLifecycle.apply(new AccountDebitedEvent(this.number, command.debitValue()));
    }

    @EventSourcingHandler
    public void on(final AccountCreditedEvent event) {
        this.balance += event.creditValue();
    }

    @EventSourcingHandler
    public void on(final AccountDebitedEvent event) {
        this.balance -= event.debitValue();
    }

    public String getNumber() {
        return number;
    }

    public long getBalance() {
        return balance;
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }
}
