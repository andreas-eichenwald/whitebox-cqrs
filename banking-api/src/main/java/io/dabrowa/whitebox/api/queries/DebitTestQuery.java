package io.dabrowa.whitebox.api.queries;

import java.math.BigDecimal;

public record DebitTestQuery(String accountNumber, BigDecimal debitValue) {
    public enum DebitTestResult {
        OPERATION_ALLOWED, OPERATION_FORBIDDEN
    }
}
