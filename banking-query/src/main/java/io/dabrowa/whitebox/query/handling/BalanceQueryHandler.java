package io.dabrowa.whitebox.query.handling;

import io.dabrowa.whitebox.api.queries.AccountBalanceQuery;
import io.dabrowa.whitebox.api.queries.AccountBalanceQuery.BalanceQueryResponse;
import io.dabrowa.whitebox.api.queries.AccountsInTheRedQuery;
import io.dabrowa.whitebox.api.queries.DebitTestQuery;
import io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult;
import io.dabrowa.whitebox.query.repository.BalanceRepository;
import io.dabrowa.whitebox.query.repository.OverdraftLimitRepository;
import org.axonframework.queryhandling.QueryHandler;

import java.math.BigDecimal;
import java.util.Set;

import static io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult.OPERATION_ALLOWED;
import static io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult.OPERATION_FORBIDDEN;

public class BalanceQueryHandler {
    public static class AccountDoesNotExist extends Exception {
        private AccountDoesNotExist(final String number) {
            super("Account " + number + " does not exist");
        }
    }

    private final BalanceRepository balanceRepository;
    private final OverdraftLimitRepository overdraftLimitRepository;

    public BalanceQueryHandler(final BalanceRepository balanceRepository,
                               final OverdraftLimitRepository overdraftLimitRepository) {
        this.balanceRepository = balanceRepository;
        this.overdraftLimitRepository = overdraftLimitRepository;
    }

    @QueryHandler
    public BalanceQueryResponse handle(final AccountBalanceQuery query) throws AccountDoesNotExist {
        return balanceRepository.getBalance(query.accountId())
                .map(BalanceQueryResponse::new)
                .orElseThrow(() -> new AccountDoesNotExist(query.accountId()));
    }

    @QueryHandler
    public DebitTestResult handle(final DebitTestQuery query) throws AccountDoesNotExist {
        final var balanceOpt = balanceRepository.getBalance(query.accountNumber());
        final var overdraftLimitOpt = overdraftLimitRepository.overdraftLimitFor(query.accountNumber());

        if (overdraftLimitOpt.isEmpty() || balanceOpt.isEmpty()) {
            throw new AccountDoesNotExist(query.accountNumber());
        }

        if (transactionExceedsOverdraftLimit(balanceOpt.get(), overdraftLimitOpt.get(), query.debitValue())) {
            return OPERATION_FORBIDDEN;
        }
        return OPERATION_ALLOWED;
    }

    @QueryHandler
    public AccountsInTheRedQuery.Response handle(final AccountsInTheRedQuery query) {
        return new AccountsInTheRedQuery.Response(Set.copyOf(balanceRepository.accountsWithNegativeBalance()));
    }

    private boolean transactionExceedsOverdraftLimit(final BigDecimal accountBalance,
                                                     final BigDecimal overdraftLimit,
                                                     final BigDecimal queryValue) {
        final var afterTransactionBalance = accountBalance.subtract(queryValue);
        final var lowestAllowedBalance = overdraftLimit.negate();
        return afterTransactionBalance.compareTo(lowestAllowedBalance) < 0;
    }
}
