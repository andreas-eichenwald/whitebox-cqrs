package io.dabrowa.whitebox.command.aggregates.account

import io.dabrowa.whitebox.api.commands.CreateAccountCommand
import io.dabrowa.whitebox.api.events.AccountCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import spock.lang.Specification

class AccountAggregateTest extends Specification {
    String accountNumber = "9876-1234-8765-2345"
    def fixture = new AggregateTestFixture<>(AccountAggregate)

    def "initial balance cannot be negative"() {
        when:
        new AccountAggregate(new CreateAccountCommand(accountNumber, new BigDecimal("-1.123"), BigDecimal.ZERO))

        then:
        def e = thrown(AccountValidation.AccountValidationException)
        e.message == "Account's initial balance cannot be negative"
    }

    def "overdraft limit cannot be negative"() {
        when:
        new AccountAggregate(new CreateAccountCommand(accountNumber, BigDecimal.ZERO, new BigDecimal("-1.123")))

        then:
        def e = thrown(AccountValidation.AccountValidationException)
        e.message == "Account's overdraft limit cannot be negative"
    }

    def "creation event populates aggregate's state"() {
        given:
        def sut = new AccountAggregate()

        when:
        sut.handle(new AccountCreatedEvent(accountNumber, new BigDecimal("12"), new BigDecimal("1000")))

        then:
        sut.number == accountNumber
        sut.balance == new BigDecimal("12")
        sut.overdraftLimit == new BigDecimal("1000")
    }
}
