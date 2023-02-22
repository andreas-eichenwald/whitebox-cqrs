package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.query.handling.BalanceQueryHandler;
import io.dabrowa.whitebox.query.handling.TransactionsQueryHandler;
import io.dabrowa.whitebox.query.projection.AccountBalanceProjector;
import io.dabrowa.whitebox.query.projection.OverdraftProjector;
import io.dabrowa.whitebox.query.projection.TransactionProjector;
import io.dabrowa.whitebox.query.repository.*;
import io.dabrowa.whitebox.query.repository.BalanceRepository.NonNegativeValuesRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class SpringAppConfiguration {

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

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool("localhost", 6379);
    }

    @Bean
    public BalanceRepository repository(final JedisPool jedisPool) {
        return new NonNegativeValuesRepository(new RedisBalanceRepository(jedisPool));
    }
}
