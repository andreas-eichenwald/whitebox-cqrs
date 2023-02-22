package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.command.aggregates.account.AccountNumberRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringTestConfiguration {

    @Bean
    @Primary
    public AccountNumberRegistry accountNumberRegistry() {
        return new TestAccountNumberProvider();
    }
}
