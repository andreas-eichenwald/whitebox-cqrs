package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.query.handling.BalanceQueryHandler;
import io.dabrowa.whitebox.query.projection.AccountBalanceProjector;
import io.dabrowa.whitebox.query.projection.OverdraftProjection;
import io.dabrowa.whitebox.query.repository.BalanceRepository;
import io.dabrowa.whitebox.query.repository.InMemoryBalanceRepository;
import io.dabrowa.whitebox.query.repository.InMemoryLimitRepository;
import io.dabrowa.whitebox.query.repository.OverdraftLimitRepository;
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
    public OverdraftProjection overdraftProjection(final OverdraftLimitRepository repository) {
        return new OverdraftProjection(repository);
    }
}
