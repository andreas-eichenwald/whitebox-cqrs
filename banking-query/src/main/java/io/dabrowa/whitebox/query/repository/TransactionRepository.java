package io.dabrowa.whitebox.query.repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public interface TransactionRepository {
    /**
     * @param accountNumber    Unique identifier of account performing the transaction
     * @param timestamp        Transaction timestamp, as registered in upstream source
     * @param transactionValue Monetary value of transaction. Negative for debit transactions, positive for credit ones.
     */
    default void record(final String accountNumber, final Instant timestamp, final long transactionValue) {
        this.record(accountNumber, new Transaction(timestamp, transactionValue));
    }

    void record(final String accountNumber, final Transaction transaction);

    /**
     * @return Ordered list of transactions made by given account after (timestamps strictly greater than),
     * sorted chronologically (by timestamps ascending)
     */
    List<Transaction> fetchLaterThan(final String accountNumber, final Instant timestamp);

    record Transaction(Instant timestamp, long value) implements Comparable<Transaction> {

        @Override
        public int compareTo(Transaction o) {
            return this.timestamp.compareTo(o.timestamp());
        }
    }
}
