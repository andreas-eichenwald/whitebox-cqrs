package io.dabrowa.whitebox.app.axontest


import io.dabrowa.whitebox.api.commands.CreateAccountCommand
import io.dabrowa.whitebox.api.events.AccountCreatedEvent

import static io.dabrowa.whitebox.command.aggregates.account.AccountValidation.AccountValidationException

class AccountCreationE2ETest extends AxonBaseE2ETest {
    String accountNumber
    BigDecimal hundred = new BigDecimal(100)
    BigDecimal negativeHundred = new BigDecimal(-100)

    def setup() {
        accountNumber = testAccountNumberProvider.nextAvailable
    }

    def "cannot create account with negative initial balance"() {
        given:
        def testCase = fixture.givenNoPriorActivity()

        when:
        testCase = testCase.when(new CreateAccountCommand(accountNumber, negativeHundred, hundred))

        then:
        testCase.expectException(AccountValidationException)
    }

    def "cannot create account with negative overdraft limit"() {
        given:
        def testCase = fixture.givenNoPriorActivity()

        when:
        testCase = testCase.when(new CreateAccountCommand(accountNumber, hundred, negativeHundred))

        then:
        testCase.expectException(AccountValidationException)
    }

    def "creating valid account results in AccountCreatedEvent"() {
        given:
        def testCase = fixture.givenNoPriorActivity()

        when:
        testCase = testCase.when(new CreateAccountCommand(accountNumber, hundred, hundred))

        then:
        testCase.expectEvents(new AccountCreatedEvent(accountNumber, hundred, hundred))
    }
}
