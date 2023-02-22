package io.dabrowa.whitebox.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringTestConfiguration {

    @Bean
    @Primary
    public TestAccountNumberProvider accountNumberRegistry() {
        return new TestAccountNumberProvider();
    }
}
