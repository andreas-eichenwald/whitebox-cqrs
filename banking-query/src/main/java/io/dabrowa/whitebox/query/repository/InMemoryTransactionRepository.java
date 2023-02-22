package io.dabrowa.whitebox.query.repository;

import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.Transaction;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, SortedMap<Long, Transaction>> perAccountTransactionLogs;

    public InMemoryTransactionRepository() {
        perAccountTransactionLogs = new ConcurrentHashMap<>();
    }

    @Override
    public void record(final String accountNumber, final Transaction transaction) {
        logFor(accountNumber).put(transaction.occurredAtEpochMillis(), transaction);
    }

    @Override
    public List<Transaction> fetchLaterThan(final String accountNumber, final long epochMillis) {
        final var accountTransactions = logFor(accountNumber)
                .tailMap(epochMillis + 1)
                .values();
        return List.copyOf(accountTransactions);
    }

    private SortedMap<Long, Transaction> logFor(final String accountNumber) {
        return perAccountTransactionLogs.computeIfAbsent(
                accountNumber,
                (ignored) -> new ConcurrentSkipListMap<>()
        );
    }
}
