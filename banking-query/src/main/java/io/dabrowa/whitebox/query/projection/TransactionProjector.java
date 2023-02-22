package io.dabrowa.whitebox.query.projection;

import io.dabrowa.whitebox.api.events.AccountCreditedEvent;
import io.dabrowa.whitebox.api.events.AccountDebitedEvent;
import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.Transaction;
import io.dabrowa.whitebox.query.repository.TransactionRepository;
import org.axonframework.eventsourcing.EventSourcingHandler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    static long epochMillisAtWarsawOffset(String dateTime) {
        return LocalDateTime.parse(dateTime).toInstant(ZoneOffset.of("+01:00")).toEpochMilli();
    }

    public static void main(String[] args) {
        System.out.println(epochMillisAtWarsawOffset("2023-01-10T11:00:00"));
        System.out.println(epochMillisAtWarsawOffset("2023-01-11T15:00:00"));
        System.out.println(epochMillisAtWarsawOffset("2023-01-20T22:00:00"));
        System.out.println(epochMillisAtWarsawOffset("2023-02-10T11:00:00"));
        System.out.println(epochMillisAtWarsawOffset("2023-02-11T15:00:00"));
        System.out.println(epochMillisAtWarsawOffset("2023-02-20T22:00:00"));
    }
}
