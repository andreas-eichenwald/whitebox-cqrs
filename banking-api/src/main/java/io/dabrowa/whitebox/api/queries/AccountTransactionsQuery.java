package io.dabrowa.whitebox.api.queries;

/**
 * @param epochMillis Starting date represented as number milliseconds since Epoch
 * @see java.time.Instant#ofEpochMilli(long)
 */
public record AccountTransactionsQuery(String accountNumber, long epochMillis) {
}
