package io.dabrowa.whitebox.command.aggregates.account

import spock.lang.Specification
import spock.lang.Unroll

class AccountValidationTest extends Specification {
    def sut = new AccountValidation()

    def "initial balance cannot be negative"() {
        when:
        sut.validateInitialBalance(new BigDecimal("-1.123"))

        then:
        def e = thrown(AccountValidation.AccountValidationException)
        e.message == "Account's initial balance cannot be negative"
    }

    @Unroll
    def "initial balance can be zero or positive"() {
        when:
        sut.validateInitialBalance(new BigDecimal(initialBalance))

        then:
        noExceptionThrown()

        where:
        initialBalance << ["0", "0.00", "0.00001", "123.55"]
    }

    def "overdraft limit cannot be negative"() {
        when:
        sut.validateOverdraftLimit(new BigDecimal("-1.123"))

        then:
        def e = thrown(AccountValidation.AccountValidationException)
        e.message == "Account's overdraft limit cannot be negative"
    }

    @Unroll
    def "overdraft limit can be zero or positive"() {
        when:
        sut.validateOverdraftLimit(new BigDecimal(initialBalance))

        then:
        noExceptionThrown()

        where:
        initialBalance << ["0", "0.00", "0.00001", "123.55"]
    }
}
