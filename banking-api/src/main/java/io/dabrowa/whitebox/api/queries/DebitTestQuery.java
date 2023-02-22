package io.dabrowa.whitebox.api.queries;

public record DebitTestQuery(String accountNumber, long debitValue) {
    public enum DebitTestResult {
        OPERATION_ALLOWED, OPERATION_FORBIDDEN
    }
}
