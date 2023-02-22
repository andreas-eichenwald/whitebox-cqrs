package io.dabrowa.whitebox.query.projection;

import io.dabrowa.whitebox.api.events.AccountCreatedEvent;
import io.dabrowa.whitebox.api.events.AccountCreditedEvent;
import io.dabrowa.whitebox.api.events.AccountDebitedEvent;
import io.dabrowa.whitebox.query.repository.BalanceRepository;
import org.axonframework.eventsourcing.EventSourcingHandler;

public class AccountBalanceProjector {

    private final BalanceRepository balanceRepository;

    public AccountBalanceProjector(final BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @EventSourcingHandler
    public void on(final AccountCreatedEvent event) {
        balanceRepository.credit(event.accountId(), event.initialBalance());
    }

    @EventSourcingHandler
    public void on(final AccountDebitedEvent event) {
        balanceRepository.debit(event.accountNumber(), event.debitValue());
    }

    @EventSourcingHandler
    public void on(final AccountCreditedEvent event) {
        balanceRepository.credit(event.accountNumber(), event.creditValue());
    }
}
