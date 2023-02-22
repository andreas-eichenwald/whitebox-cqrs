package io.dabrowa.whitebox.domain.aggregates.account;

import io.dabrowa.whitebox.domain.commands.CreateAccountCommand;
import io.dabrowa.whitebox.domain.events.AccountCreatedEvent;
import io.dabrowa.whitebox.domain.events.Event;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static io.dabrowa.whitebox.domain.aggregates.account.AccountValidation.AccountValidationException;

@Aggregate
public class AccountAggregate {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAggregate.class);

    @AggregateIdentifier
    private String number;

    private long initialBalance;

    private long overdraftLimit;

    @CommandHandler
    public static AccountAggregate handle(final CreateAccountCommand command, final Consumer<Event> eventHandler) throws AccountValidationException {
        LOGGER.debug("Creating account {}", command.accountId());

        final AccountValidation accountValidation = new AccountValidation();
        accountValidation.validateNumber(command.accountId());
        accountValidation.validateInitialBalance(command.initialBalance());
        accountValidation.validateOverdraftLimit(command.overdraftLimit());

        eventHandler.accept(
                new AccountCreatedEvent(
                        command.accountId(),
                        command.initialBalance(),
                        command.overdraftLimit()
                )
        );
        return new AccountAggregate();
    }

    private AccountAggregate() {
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
