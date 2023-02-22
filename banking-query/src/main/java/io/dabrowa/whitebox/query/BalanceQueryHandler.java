package io.dabrowa.whitebox.query;

import io.dabrowa.whitebox.domain.queries.AccountBalanceQuery;

public class BalanceQueryHandler {
    public static class AccountDoesNotExist extends Exception {
        private AccountDoesNotExist(final String number) {
            super("Account " + number + " does not exist");
        }
    }

    private final BalanceRepository repository;

    public BalanceQueryHandler(BalanceRepository repository) {
        this.repository = repository;
    }

    public long handle(final AccountBalanceQuery query) throws AccountDoesNotExist {
        return repository.getBalance(query.accountId())
                .orElseThrow(() -> new AccountDoesNotExist(query.accountId()));
    }
}
