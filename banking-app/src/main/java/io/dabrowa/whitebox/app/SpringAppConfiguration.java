package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.query.handling.BalanceQueryHandler;
import io.dabrowa.whitebox.query.handling.TransactionsQueryHandler;
import io.dabrowa.whitebox.query.projection.AccountBalanceProjector;
import io.dabrowa.whitebox.query.projection.OverdraftProjector;
import io.dabrowa.whitebox.query.projection.TransactionProjector;
import io.dabrowa.whitebox.query.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAppConfiguration {

    @Bean
    public BalanceRepository balanceRepository() {
        return new InMemoryBalanceRepository();
    }

    @Bean
    public OverdraftLimitRepository overdraftLimitRepository() {
        return new InMemoryLimitRepository();
    }

    @Bean
    public BalanceQueryHandler balanceQueryHandler(final BalanceRepository balanceRepository,
                                                   final OverdraftLimitRepository limitRepository) {
        return new BalanceQueryHandler(balanceRepository, limitRepository);
    }

    @Bean
    public AccountBalanceProjector accountBalanceProjector(final BalanceRepository balanceRepository) {
        return new AccountBalanceProjector(balanceRepository);
    }

    @Bean
    public OverdraftProjector overdraftProjection(final OverdraftLimitRepository repository) {
        return new OverdraftProjector(repository);
    }

    @Bean
    public TransactionRepository transactionRepository() {
        return new InMemoryTransactionRepository();
    }

    @Bean
    public TransactionsQueryHandler transactionsQueryHandler(final TransactionRepository transactionRepository) {
        return new TransactionsQueryHandler(transactionRepository);
    }

    @Bean
    public TransactionProjector transactionProjector(final TransactionRepository transactionRepository) {
        return new TransactionProjector(transactionRepository);
    }
}
