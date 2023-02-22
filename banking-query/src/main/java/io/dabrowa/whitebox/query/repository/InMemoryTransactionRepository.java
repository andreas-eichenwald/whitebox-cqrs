package io.dabrowa.whitebox.query.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, SortedSet<Transaction>> perAccountTransactionLogs;

    public InMemoryTransactionRepository() {
        perAccountTransactionLogs = new ConcurrentHashMap<>();
    }

    @Override
    public void record(final String accountNumber, final Transaction transaction) {
        logFor(accountNumber).add(transaction);
    }

    @Override
    public List<Transaction> fetchLaterThan(final String accountNumber, final Instant timestamp) {
        // TODO: This fetching mechanism with dummy transaction as comparable element is ugly, refactor
        final var transactions = logFor(accountNumber).tailSet(new Transaction(timestamp.plusNanos(1), 1));
        return List.copyOf(transactions);
    }

    private SortedSet<Transaction> logFor(final String accountNumber) {
        return perAccountTransactionLogs.computeIfAbsent(accountNumber, (ignored) -> new ConcurrentSkipListSet<>());
    }
}
