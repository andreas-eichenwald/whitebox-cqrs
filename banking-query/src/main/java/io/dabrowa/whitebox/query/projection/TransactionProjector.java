package io.dabrowa.whitebox.query.projection;

import io.dabrowa.whitebox.api.events.AccountCreditedEvent;
import io.dabrowa.whitebox.api.events.AccountDebitedEvent;
import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.Transaction;
import io.dabrowa.whitebox.query.repository.TransactionRepository;
import org.axonframework.eventsourcing.EventSourcingHandler;

import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionType.CREDIT;
import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionType.DEBIT;

public class TransactionProjector {
    private final TransactionRepository transactionRepository;

    public TransactionProjector(final TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @EventSourcingHandler
    public void handle(final AccountCreditedEvent event) {
        transactionRepository.record(
                event.accountNumber(),
                new Transaction(event.creditedAtEpochMillis(), CREDIT, event.creditValue())
        );
    }

    @EventSourcingHandler
    public void handle(final AccountDebitedEvent event) {
        transactionRepository.record(
                event.accountNumber(),
                new Transaction(event.creditedAtEpochMillis(), DEBIT, event.debitValue())
        );
    }
}
