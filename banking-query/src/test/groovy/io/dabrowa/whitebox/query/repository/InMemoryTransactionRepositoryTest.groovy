package io.dabrowa.whitebox.query.repository


import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime

import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.Transaction
import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionType.CREDIT
import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionType.DEBIT
import static java.time.ZoneOffset.UTC

class InMemoryTransactionRepositoryTest extends Specification {
    def sut = new InMemoryTransactionRepository()
    def accountNumber = "1234-2345-3456-4567"
    def otherAccountNumber = "9999-8888-7777-6666"
    def epochMillis = Instant.EPOCH.toEpochMilli()

    def "empty response after object creation"() {
        expect:
        sut.fetchLaterThan(accountNumber, epochMillis).empty
    }

    def "non-empty state for other account - answer should be empty"() {
        given:
        sut.record(
                otherAccountNumber,
                new Transaction(epochMillis + 10, DEBIT, BigDecimal.ONE)
        )

        expect:
        sut.fetchLaterThan(accountNumber, epochMillis).empty
    }

    def "only includes transactions for given account"() {
        given:
        sut.record(
                accountNumber,
                new Transaction(epochMillis + 10, DEBIT, BigDecimal.ONE)
        )
        sut.record(
                otherAccountNumber,
                new Transaction(epochMillis + 10, DEBIT, BigDecimal.ONE)
        )

        expect:
        with(sut.fetchLaterThan(accountNumber, epochMillis)) {
            it.size() == 1
            it == [new Transaction(epochMillis + 10, DEBIT, BigDecimal.ONE)]
        }

    }

    def "returns only transactions strictly later than specified point in time"() {
        given:
        def dateMillis = asMillis("2023-02-22T15:00:00")

        sut.record(
                accountNumber,
                new Transaction(dateMillis, DEBIT, BigDecimal.ONE)
        )
        sut.record(
                accountNumber,
                new Transaction(dateMillis + 1, DEBIT, BigDecimal.ONE)
        )

        expect:
        with(sut.fetchLaterThan(accountNumber, dateMillis)) {
            it.size() == 1
            it == [new Transaction(dateMillis + 1, DEBIT, BigDecimal.ONE)]
        }
    }

    def "returns correct list of transactions given starting point"() {
        given:
        def transactions = [
                new Transaction(asMillis("2022-01-01T10:00"), DEBIT, BigDecimal.TEN),
                new Transaction(asMillis("2022-06-01T10:00"), CREDIT, BigDecimal.ONE),
                new Transaction(asMillis("2022-12-01T14:00"), DEBIT, new BigDecimal("44")),
                new Transaction(asMillis("2023-01-01T15:45"), CREDIT, new BigDecimal("555")),
                new Transaction(asMillis("2023-02-15T16:50"), DEBIT, new BigDecimal("12.12")),
                new Transaction(asMillis("2023-02-22T15:00"), CREDIT, new BigDecimal("69"))
        ]
        transactions.each { sut.record(accountNumber, it) }

        expect: 'correct result for all transactions'
        with(sut.fetchLaterThan(accountNumber, asMillis("2020-05-13T00:00"))) {
            it.size() == 6
            it == [
                    new Transaction(asMillis("2022-01-01T10:00"), DEBIT, BigDecimal.TEN),
                    new Transaction(asMillis("2022-06-01T10:00"), CREDIT, BigDecimal.ONE),
                    new Transaction(asMillis("2022-12-01T14:00"), DEBIT, new BigDecimal("44")),
                    new Transaction(asMillis("2023-01-01T15:45"), CREDIT, new BigDecimal("555")),
                    new Transaction(asMillis("2023-02-15T16:50"), DEBIT, new BigDecimal("12.12")),
                    new Transaction(asMillis("2023-02-22T15:00"), CREDIT, new BigDecimal("69"))
            ]
        }

        and: 'correct result for all but one transaction'
        with(sut.fetchLaterThan(accountNumber, asMillis("2022-05-13T00:00"))) {
            it.size() == 5
            it == [
                    new Transaction(asMillis("2022-06-01T10:00"), CREDIT, BigDecimal.ONE),
                    new Transaction(asMillis("2022-12-01T14:00"), DEBIT, new BigDecimal("44")),
                    new Transaction(asMillis("2023-01-01T15:45"), CREDIT, new BigDecimal("555")),
                    new Transaction(asMillis("2023-02-15T16:50"), DEBIT, new BigDecimal("12.12")),
                    new Transaction(asMillis("2023-02-22T15:00"), CREDIT, new BigDecimal("69"))
            ]
        }

        and: 'correct result for just one transaction'
        with(sut.fetchLaterThan(accountNumber, asMillis("2023-02-20T22:00"))) {
            it.size() == 1
            it == [
                    new Transaction(asMillis("2023-02-22T15:00"), CREDIT, new BigDecimal("69"))
            ]
        }
    }

    def "result is sorted no matter which order transactions are recorded in"() {
        def transactions = [
                new Transaction(asMillis("2022-12-01T14:00"), DEBIT, new BigDecimal("44")),
                new Transaction(asMillis("2022-01-01T10:00"), DEBIT, BigDecimal.TEN),
                new Transaction(asMillis("2022-06-01T10:00"), CREDIT, BigDecimal.ONE)
        ]
        transactions.each { sut.record(accountNumber, it) }

        expect: 'correct order of transactions'
        with(sut.fetchLaterThan(accountNumber, epochMillis)) {
            it.collect { transaction -> transaction.occurredAtEpochMillis() } == [
                    asMillis("2022-01-01T10:00"),
                    asMillis("2022-06-01T10:00"),
                    asMillis("2022-12-01T14:00")
            ]
        }
    }

    long asMillis(String s) {
        LocalDateTime.parse(s).toInstant(UTC).toEpochMilli()
    }
}
