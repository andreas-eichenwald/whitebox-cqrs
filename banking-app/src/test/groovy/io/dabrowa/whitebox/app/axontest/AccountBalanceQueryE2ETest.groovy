package io.dabrowa.whitebox.app.axontest

import io.dabrowa.whitebox.api.commands.CreateAccountCommand
import io.dabrowa.whitebox.api.commands.CreditAccountCommand
import io.dabrowa.whitebox.api.commands.DebitAccountCommand
import io.dabrowa.whitebox.api.queries.AccountBalanceQuery
import io.dabrowa.whitebox.api.queries.AccountsInTheRedQuery
import io.dabrowa.whitebox.api.queries.DebitTestQuery
import spock.lang.Unroll

import static io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult.OPERATION_ALLOWED
import static io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult.OPERATION_FORBIDDEN
import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await

class AccountBalanceQueryE2ETest extends AxonBaseE2ETest {

    def "AccountBalanceQuery returns balance for the account"() {
        given:
        def accountNumber = testAccountNumberProvider.nextAvailable

        commandGateway.sendAndWait(new CreateAccountCommand(accountNumber, 100L, 100L))
        commandGateway.send(new CreditAccountCommand(accountNumber, 22L))
        commandGateway.send(new DebitAccountCommand(accountNumber, 11L))
        commandGateway.send(new CreditAccountCommand(accountNumber, 33L))

        when:
        def query = new AccountBalanceQuery(accountNumber)

        then:
        await().atMost(5, SECONDS).until {
            def result = queryGateway.query(query, AccountBalanceQuery.BalanceQueryResponse).get()
            result == new AccountBalanceQuery.BalanceQueryResponse(144L)
        }
    }

    @Unroll
    def "DebitTestQuery returns information about balance overdraft"() {
        given:
        def accountNumber = testAccountNumberProvider.nextAvailable
        commandGateway.sendAndWait(new CreateAccountCommand(accountNumber, 65L, 33L))
        def query = new DebitTestQuery(accountNumber, debitValue)

        expect:
        queryGateway.query(query, DebitTestQuery.DebitTestResult).get() == expectedStatus

        where:
        debitValue | expectedStatus
        1          | OPERATION_ALLOWED
        65         | OPERATION_ALLOWED
        98         | OPERATION_ALLOWED
        99         | OPERATION_FORBIDDEN
        100        | OPERATION_FORBIDDEN
        250        | OPERATION_FORBIDDEN
    }

    def "AccountsInTheRedQuery returns information about accounts with negative balance"() {
        given: 'initial set of accounts with 0 balance'
        def accounts = [
                testAccountNumberProvider.nextAvailable,
                testAccountNumberProvider.nextAvailable,
                testAccountNumberProvider.nextAvailable,
                testAccountNumberProvider.nextAvailable
        ]

        accounts.each {commandGateway.sendAndWait(new CreateAccountCommand(it, 0, 100L)) }
        def query = new AccountsInTheRedQuery()

        when: 'querying list of accounts in the red'
        def result = queryGateway.query(query, AccountsInTheRedQuery.Response).get()

        then:
        result.accountNumbers().empty

        when: 'going below 0 for three accounts'
        commandGateway.sendAndWait(new DebitAccountCommand(accounts[0], 10))
        commandGateway.sendAndWait(new DebitAccountCommand(accounts[2], 100))
        commandGateway.sendAndWait(new DebitAccountCommand(accounts[3], 50))
        result = queryGateway.query(query, AccountsInTheRedQuery.Response).get()

        then: 'those account numbers are returned'
        result.accountNumbers() as Set == [accounts[0], accounts[2], accounts[3]] as Set

        when: 'two accounts pay their debt'
        commandGateway.sendAndWait(new CreditAccountCommand(accounts[0], 1000))
        commandGateway.sendAndWait(new CreditAccountCommand(accounts[2], 1000))
        result = queryGateway.query(query, AccountsInTheRedQuery.Response).get()

        then: 'they are removed from the offender list'
        result.accountNumbers() as Set == [accounts[3]] as Set
    }
}
