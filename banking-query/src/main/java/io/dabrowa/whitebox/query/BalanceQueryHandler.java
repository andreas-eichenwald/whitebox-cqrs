package io.dabrowa.whitebox.query;

import io.dabrowa.whitebox.command.queries.AccountBalanceQuery;
import io.dabrowa.whitebox.command.queries.DebitTestQuery;
import io.dabrowa.whitebox.command.queries.DebitTestQuery.DebitTestResult;
import io.dabrowa.whitebox.query.repository.BalanceRepository;
import io.dabrowa.whitebox.query.repository.OverdraftLimitRepository;
import org.axonframework.queryhandling.QueryHandler;

import static io.dabrowa.whitebox.command.queries.DebitTestQuery.DebitTestResult.OPERATION_ALLOWED;
import static io.dabrowa.whitebox.command.queries.DebitTestQuery.DebitTestResult.OPERATION_FORBIDDEN;

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
    public long handle(final AccountBalanceQuery query) throws AccountDoesNotExist {
        return balanceRepository.getBalance(query.accountId())
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

    private boolean transactionExceedsOverdraftLimit(final long accountBalance,
                                                     final long overdraftLimit,
                                                     final long queryValue) {
        final var afterTransactionBalance = accountBalance - queryValue;
        return afterTransactionBalance < 0 && Math.abs(afterTransactionBalance) > overdraftLimit;
    }
}
