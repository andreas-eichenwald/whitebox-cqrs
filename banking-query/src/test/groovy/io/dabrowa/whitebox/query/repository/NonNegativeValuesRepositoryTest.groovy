package io.dabrowa.whitebox.query.repository

import spock.lang.Specification
import spock.lang.Unroll

class NonNegativeValuesRepositoryTest extends Specification {
    def delegate = Mock(BalanceRepository)
    def sut = new BalanceRepository.NonNegativeValuesRepository(delegate)

    def accountNumber = "1234"

    def "credit value cannot be negative"() {
        when:
        sut.credit(accountNumber, new BigDecimal("-1.1"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Value cannot be negative"
    }

    @Unroll
    def "credit value can be non-negative"() {
        when:
        sut.credit(accountNumber, new BigDecimal(value))

        then:
        noExceptionThrown()
        1 * delegate.credit(accountNumber, new BigDecimal(value))

        where:
        value << ["0.0000", "123"]
    }

    def "debit value cannot be negative"() {
        when:
        sut.debit(accountNumber, new BigDecimal("-1.1"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Value cannot be negative"
    }

    @Unroll
    def "debit value can be non-negative"() {
        when:
        sut.debit(accountNumber, new BigDecimal(value))

        then:
        noExceptionThrown()
        1 * delegate.debit(accountNumber, new BigDecimal(value))

        where:
        value << ["0.0000", "123"]
    }
}
