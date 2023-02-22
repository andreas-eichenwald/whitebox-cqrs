package io.dabrowa.whitebox.domain.aggregates

import io.dabrowa.whitebox.domain.aggregates.account.AccountFactory
import io.dabrowa.whitebox.domain.aggregates.account.AccountNumberRegistry
import io.dabrowa.whitebox.domain.commands.CreateAccountCommand
import io.dabrowa.whitebox.domain.events.AccountCreatedEvent
import io.dabrowa.whitebox.domain.events.Event
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Consumer

import static io.dabrowa.whitebox.domain.aggregates.account.AccountValidation.AccountValidationException

class AccountFactoryTest extends Specification {
    def eventHandler = Mock(Consumer<Event>)

    def numberProvider = Mock(AccountNumberRegistry) {
        getNextAvailable() >> "1234-2345-3456-4567"
    }

    def sut = new AccountFactory(numberProvider, eventHandler)

    @Unroll
    def "cannot create instance with invalid balance"() {
        given:
        def createCommand = new CreateAccountCommand(initialBalance, 1L)

        when:
        sut.handle(createCommand)

        then:
        def e = thrown(AccountValidationException)
        e.message == "Account's initial balance must be positive"
        0 * numberProvider.nextAvailable

        where:
        initialBalance << [0, -1]
    }

    @Unroll
    def "cannot create instance with invalid overdraft limit"() {
        given:
        def createCommand = new CreateAccountCommand(1L, overdraftLimit)

        when:
        sut.handle(createCommand)

        then:
        def e = thrown(AccountValidationException)
        e.message == "Account's overdraft limit must be positive"
        0 * numberProvider.nextAvailable

        where:
        overdraftLimit << [0, -1]
    }

    def "successful event creation triggers create event"() {
        given:
        def createCommand = new CreateAccountCommand(10L, 100L)

        when:
        sut.handle(createCommand)

        then:
        1 * eventHandler.accept(new AccountCreatedEvent("1234-2345-3456-4567", 10L, 100L))

    }
}
