package io.dabrowa.whitebox.api.queries;

import java.math.BigDecimal;

public record AccountBalanceQuery(String accountId) {
    public record BalanceQueryResponse(BigDecimal balance) {
    }
}
