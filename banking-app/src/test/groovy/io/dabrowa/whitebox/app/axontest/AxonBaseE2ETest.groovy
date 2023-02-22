package io.dabrowa.whitebox.app.axontest


import io.dabrowa.whitebox.api.commands.CreateAccountCommand
import io.dabrowa.whitebox.app.Main
import io.dabrowa.whitebox.app.SpringAppConfiguration
import io.dabrowa.whitebox.app.TestAccountNumberProvider
import io.dabrowa.whitebox.command.aggregates.account.AccountAggregate
import io.dabrowa.whitebox.query.repository.InMemoryBalanceRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.queryhandling.QueryGateway
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
    QueryGateway queryGateway

    @Autowired
    CommandGateway commandGateway

    @Autowired
    EventGateway eventGateway

    @Autowired
    TestAccountNumberProvider testAccountNumberProvider

    @Autowired
    InMemoryBalanceRepository inMemoryBalanceRepository

    def setup() {
        fixture = new AggregateTestFixture<>(AccountAggregate)
        inMemoryBalanceRepository.cleanup()
    }

    String setupAccount(BigDecimal initialBalance, BigDecimal overdraftLimit) {
        def accountNumber = testAccountNumberProvider.nextAvailable
        commandGateway.sendAndWait(new CreateAccountCommand(accountNumber, initialBalance, overdraftLimit))
        return accountNumber
    }
}
