package io.dabrowa.whitebox.domain.aggregates

import io.dabrowa.whitebox.domain.aggregates.account.AccountAggregate
import io.dabrowa.whitebox.domain.commands.CreateAccountCommand
import io.dabrowa.whitebox.domain.events.AccountCreatedEvent
import io.dabrowa.whitebox.domain.events.Event
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Consumer

import static io.dabrowa.whitebox.domain.aggregates.account.AccountValidation.AccountValidationException

class AccountAggregateTest extends Specification {
    def validNumber = "1234-1234-1234-1234"
    def messageConsumer = Mock(Consumer<Event>)

    @Unroll
    def "cannot create instance with invalid account number"() {
        given:
        def createCommand = new CreateAccountCommand(accountNumber, 1L, 1L)

        when:
        AccountAggregate.handle(createCommand, messageConsumer)

        then:
        def e = thrown(AccountValidationException)
        e.message == "Invalid account number: $accountNumber"
        0 * messageConsumer.accept

        where:
        accountNumber << ["invalid number 123", "1111-2222-3333-444", "1111-2222-3333-44444", "1111-2222-3333-4444-"]
    }

    @Unroll
    def "cannot create instance with invalid balance"() {
        given:
        def createCommand = new CreateAccountCommand(validNumber, initialBalance, 1L)

        when:
        AccountAggregate.handle(createCommand, messageConsumer)

        then:
        def e = thrown(AccountValidationException)
        e.message == "Account's initial balance must be positive"
        0 * messageConsumer.accept

        where:
        initialBalance << [0, -1]
    }

    @Unroll
    def "cannot create instance with invalid overdraft limit"() {
        given:
        def createCommand = new CreateAccountCommand(validNumber, 1L, overdraftLimit)

        when:
        AccountAggregate.handle(createCommand, messageConsumer)

        then:
        def e = thrown(AccountValidationException)
        e.message == "Account's overdraft limit must be positive"
        0 * messageConsumer.accept

        where:
        overdraftLimit << [0, -1]
    }

    def "successful event creation triggers create event"() {
        given:
        def createCommand = new CreateAccountCommand(
                validNumber,
                10L,
                100L
        )

        when:
        def instance = AccountAggregate.handle(createCommand, messageConsumer)

        then:
        instance != null
        1 * messageConsumer.accept(new AccountCreatedEvent("1234-1234-1234-1234", 10L, 100L))
    }
}
