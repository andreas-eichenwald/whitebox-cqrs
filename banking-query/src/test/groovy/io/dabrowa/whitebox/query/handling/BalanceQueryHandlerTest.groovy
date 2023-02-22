package io.dabrowa.whitebox.query.handling

import io.dabrowa.whitebox.api.queries.AccountBalanceQuery
import io.dabrowa.whitebox.api.queries.AccountsInTheRedQuery
import io.dabrowa.whitebox.api.queries.DebitTestQuery
import io.dabrowa.whitebox.query.repository.BalanceRepository
import io.dabrowa.whitebox.query.repository.OverdraftLimitRepository
import spock.lang.Specification
import spock.lang.Unroll

import static io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult.OPERATION_ALLOWED
import static io.dabrowa.whitebox.api.queries.DebitTestQuery.DebitTestResult.OPERATION_FORBIDDEN
import static io.dabrowa.whitebox.query.handling.BalanceQueryHandler.AccountDoesNotExist

class BalanceQueryHandlerTest extends Specification {
    BalanceRepository balanceRepository = Mock(BalanceRepository)
    OverdraftLimitRepository overdraftLimitRepository = Mock(OverdraftLimitRepository)

    def sut = new BalanceQueryHandler(balanceRepository, overdraftLimitRepository)

    def "throws exception when querying balance for non-existing account"() {
        given:
        String accountNumber = "1234"
        1 * balanceRepository.getBalance(accountNumber) >> Optional.empty()

        when:
        sut.handle(new AccountBalanceQuery(accountNumber))

        then:
        def e = thrown(AccountDoesNotExist)
        e.message == "Account 1234 does not exist"
    }

    @Unroll
    def "throws exception when either balance or overdraft limit are missing during debit test query"() {
        given:
        String accountNumber = "12345"
        balanceRepository.getBalance(accountNumber) >> balanceResponse
        overdraftLimitRepository.overdraftLimitFor(accountNumber) >> limitResponse

        when:
        sut.handle(new DebitTestQuery(accountNumber, BigDecimal.ZERO))

        then:
        def e = thrown(AccountDoesNotExist)
        e.message == "Account 12345 does not exist"

        where:
        balanceResponse              | limitResponse
        Optional.empty()             | Optional.empty()
        Optional.empty()             | Optional.of(BigDecimal.ZERO)
        Optional.of(BigDecimal.ZERO) | Optional.empty()
    }

    @Unroll
    def "returns correct result for debit test query"() {
        given:
        String accountNumber = "12345"
        balanceRepository.getBalance(accountNumber) >> Optional.of(new BigDecimal(balance))
        overdraftLimitRepository.overdraftLimitFor(accountNumber) >> Optional.of(new BigDecimal(limit))

        expect:
        sut.handle(new DebitTestQuery(accountNumber, new BigDecimal(queryValue))) == expectedResult

        where:
        balance      | limit     | queryValue   | expectedResult
        "12345.6789" | "0"       | "12345.6789" | OPERATION_ALLOWED
        "12345.6789" | "0"       | "12345.679"  | OPERATION_FORBIDDEN
        "12345.6789" | "1"       | "12346.6789" | OPERATION_ALLOWED
        "12345.6789" | "1"       | "12347.6789" | OPERATION_FORBIDDEN
        "0"          | "1500.00" | "1000"       | OPERATION_ALLOWED
        "0"          | "1500.00" | "1499.99999" | OPERATION_ALLOWED
        "0"          | "1500.00" | "2000"       | OPERATION_FORBIDDEN
    }

    def "returns response for accounts with negative balance"() {
        given:
        1 * balanceRepository.accountsWithNegativeBalance() >> ["1", "2", "3"]

        when:
        def response = sut.handle(new AccountsInTheRedQuery())

        then:
        response.accountNumbers() == ["1", "2", "3"] as Set
    }

    def "response for accounts with negative balance contains immutable collection"() {
        given:
        1 * balanceRepository.accountsWithNegativeBalance() >> []

        when:
        def response = sut.handle(new AccountsInTheRedQuery())
        response.accountNumbers().add("9")

        then:
        thrown(UnsupportedOperationException)
        response.accountNumbers().empty
    }
}
