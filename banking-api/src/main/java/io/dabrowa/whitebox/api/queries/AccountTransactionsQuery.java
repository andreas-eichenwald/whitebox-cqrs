package io.dabrowa.whitebox.api.queries;

import java.util.List;

/**
 * @param epochMillis Starting date represented as number milliseconds since Epoch
 * @see java.time.Instant#ofEpochMilli(long)
 */
public record AccountTransactionsQuery(String accountNumber, long epochMillis) {
    public record Transaction(long occurredAtEpochMillis, TransactionType type, long transactionValue) {
    }

    public enum TransactionType {
        DEBIT, CREDIT
    }

    public record TransactionsQueryResponse(List<Transaction> transactions) {
    }
}
