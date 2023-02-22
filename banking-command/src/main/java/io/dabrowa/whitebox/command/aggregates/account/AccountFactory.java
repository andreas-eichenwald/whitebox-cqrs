package io.dabrowa.whitebox.command.aggregates.account;

import io.dabrowa.whitebox.api.commands.CreateAccountCommand;
import io.dabrowa.whitebox.api.events.AccountCreatedEvent;
import io.dabrowa.whitebox.api.events.Event;
import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class AccountFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountFactory.class);

    private final AccountNumberRegistry numberRegistry;
    private final AccountValidation accountValidation;
    private final Consumer<Event> eventHandler;

    public AccountFactory(final AccountNumberRegistry numberRegistry, final Consumer<Event> eventHandler) {
        this.numberRegistry = numberRegistry;
        this.eventHandler = eventHandler;
        this.accountValidation = new AccountValidation();
    }

    @CommandHandler
    public void handle(final CreateAccountCommand command) throws AccountValidation.AccountValidationException {
        accountValidation.validateInitialBalance(command.initialBalance());
        accountValidation.validateOverdraftLimit(command.overdraftLimit());

        final var accountNumber = numberRegistry.getNextAvailable();
        LOGGER.debug("Creating account {}", accountNumber);

        eventHandler.accept(
                new AccountCreatedEvent(
                        accountNumber,
                        command.initialBalance(),
                        command.overdraftLimit()
                )
        );
    }
}
