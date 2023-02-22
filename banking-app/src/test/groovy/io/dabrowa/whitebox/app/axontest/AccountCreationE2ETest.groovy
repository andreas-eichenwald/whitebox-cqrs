package io.dabrowa.whitebox.app.axontest

import io.dabrowa.whitebox.api.commands.CreateAccountCommand
import io.dabrowa.whitebox.api.events.AccountCreatedEvent
import io.dabrowa.whitebox.app.Main
import io.dabrowa.whitebox.app.SpringAppConfiguration
import org.springframework.boot.test.context.SpringBootTest

import static io.dabrowa.whitebox.command.aggregates.account.AccountValidation.AccountValidationException
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE, classes = [Main.class, SpringAppConfiguration.class])
class AccountCreationE2ETest extends AxonBaseE2ETest {

    def "cannot create account with negative initial balance"() {
        given:
        def testCase = fixture.givenNoPriorActivity()

        when:
        testCase = testCase.when(new CreateAccountCommand(-100, 100))

        then:
        testCase.expectException(AccountValidationException)
    }

    def "cannot create account with negative overdraft limit"() {
        given:
        def testCase = fixture.givenNoPriorActivity()

        when:
        testCase = testCase.when(new CreateAccountCommand(100, -100))

        then:
        testCase.expectException(AccountValidationException)
    }

    def "creating valid account results in AccountCreatedEvent"() {
        given:
        def testCase = fixture.givenNoPriorActivity()

        when:
        testCase = testCase.when(new CreateAccountCommand(100, 100))

        then:
        testCase.expectEvents(new AccountCreatedEvent(testAccountNumberProvider.lastGenerated, 100, 100))
    }
}
