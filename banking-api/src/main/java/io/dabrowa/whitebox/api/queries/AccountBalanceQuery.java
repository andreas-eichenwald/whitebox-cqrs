package io.dabrowa.whitebox.api.queries;

public record AccountBalanceQuery(String accountId) {
    public record BalanceQueryResponse(long balance) {
    }
}
