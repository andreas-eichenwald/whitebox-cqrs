package io.dabrowa.whitebox.domain.queries;

public record DebitTestQuery(String accountNumber, long debitValue) {
    public enum DebitTestResult {
        OPERATION_ALLOWED, OPERATION_FORBIDDEN
    }
}
