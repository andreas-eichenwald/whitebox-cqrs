package io.dabrowa.whitebox.command.aggregates.account;

import io.dabrowa.whitebox.api.commands.CreateAccountCommand;
import io.dabrowa.whitebox.api.commands.CreditAccountCommand;
import io.dabrowa.whitebox.api.commands.DebitAccountCommand;
import io.dabrowa.whitebox.api.events.AccountCreatedEvent;
import io.dabrowa.whitebox.api.events.AccountCreditedEvent;
import io.dabrowa.whitebox.api.events.AccountDebitedEvent;
import io.dabrowa.whitebox.command.aggregates.account.AccountValidation.AccountValidationException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;

@Aggregate
public class AccountAggregate {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAggregate.class);

    private final AccountValidation accountValidation;

    public static class InsufficientFundsException extends Exception {
    }

    @AggregateIdentifier
    private String number;

    private BigDecimal overdraftLimit;

    private BigDecimal balance;

    @CommandHandler
    public AccountAggregate(final CreateAccountCommand command) throws AccountValidationException {
        this();

        accountValidation.validateInitialBalance(command.initialBalance());
        accountValidation.validateOverdraftLimit(command.overdraftLimit());

        LOGGER.debug("Creating account {}", command.number());

        AggregateLifecycle.apply(
                new AccountCreatedEvent(
                        command.number(),
                        command.initialBalance(),
                        command.overdraftLimit()
                )
        );
    }

    AccountAggregate() {
        this.accountValidation = new AccountValidation();
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
        AggregateLifecycle.apply(
                new AccountCreditedEvent(
                        this.number,
                        command.creditValue(),
                        Instant.now().toEpochMilli()
                )
        );
    }

    @CommandHandler
    public void handle(final DebitAccountCommand command) throws InsufficientFundsException {
        accountValidation.validateDebitOperation(balance, overdraftLimit, command.debitValue());
        AggregateLifecycle.apply(
                new AccountDebitedEvent(
                        this.number,
                        command.debitValue(),
                        Instant.now().toEpochMilli()
                )
        );
    }

    @EventSourcingHandler
    public void on(final AccountCreditedEvent event) {
        this.balance = this.balance.add(event.creditValue());
    }

    @EventSourcingHandler
    public void on(final AccountDebitedEvent event) {
        this.balance = this.balance.subtract(event.debitValue());
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }
}
