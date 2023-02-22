package io.dabrowa.whitebox.query.repository;

import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.Transaction;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository {
    void record(final String accountNumber, final Transaction transaction);

    /**
     * @return Ordered list of transactions made by given account after (timestamps strictly greater than),
     * sorted chronologically (by timestamps ascending)
     */
    List<Transaction> fetchLaterThan(final String accountNumber, final long epochMillis);
}
