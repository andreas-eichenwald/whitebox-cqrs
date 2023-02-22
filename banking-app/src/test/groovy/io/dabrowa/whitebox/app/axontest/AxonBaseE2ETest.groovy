package io.dabrowa.whitebox.app.axontest

import io.dabrowa.whitebox.app.Main
import io.dabrowa.whitebox.app.SpringAppConfiguration
import io.dabrowa.whitebox.command.aggregates.account.AccountAggregate
import io.dabrowa.whitebox.command.aggregates.account.AccountFactory
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE, classes = [Main.class, SpringAppConfiguration.class])
class AxonBaseE2ETest extends Specification {

    FixtureConfiguration<AccountAggregate> fixture

    @Autowired
    AccountFactory accountFactory

    def setup() {
        fixture = new AggregateTestFixture<>(AccountAggregate)
        fixture.registerAnnotatedCommandHandler(accountFactory)
    }
}
