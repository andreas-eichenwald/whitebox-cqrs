package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.command.aggregates.account.AccountFactory;
import io.dabrowa.whitebox.command.aggregates.account.AccountNumberRegistry;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAppConfiguration {

    @Bean
    public AccountFactory accountFactory(final AccountNumberRegistry numberRegistry) {
        return new AccountFactory(numberRegistry, AggregateLifecycle::apply);
    }
}
