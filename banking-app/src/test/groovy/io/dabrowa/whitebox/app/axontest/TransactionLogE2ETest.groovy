package io.dabrowa.whitebox.app.axontest


import io.dabrowa.whitebox.api.events.AccountCreditedEvent
import io.dabrowa.whitebox.api.events.AccountDebitedEvent
import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.Transaction
import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionType.CREDIT
import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionType.DEBIT
import static io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionsQueryResponse

class TransactionLogE2ETest extends AxonBaseE2ETest {

    ZoneOffset warsawOffset = ZoneOffset.of("+01:00")

    def "AccountTransactionsQuery returns list of account's transactions"() {
        given: 'initial account without any transactions'
        String accountNumber = setupAccount(new BigDecimal(100), new BigDecimal(1000))
        def allTransactionsQuery = new AccountTransactionsQuery(accountNumber, Instant.EPOCH.toEpochMilli())

        expect: 'there are no transactions made by given account'
        queryGateway.query(allTransactionsQuery, TransactionsQueryResponse).get().transactions().empty


        when: 'transactions are inserted into log'
        def transactions = [new AccountCreditedEvent(accountNumber, new BigDecimal(15000), epochMillisAtWarsawOffset("2023-01-10T11:00:00")),
                            new AccountDebitedEvent(accountNumber, new BigDecimal(3500), epochMillisAtWarsawOffset("2023-01-11T15:00:00")),
                            new AccountDebitedEvent(accountNumber, new BigDecimal(2000), epochMillisAtWarsawOffset("2023-01-20T22:00:00")),
                            new AccountCreditedEvent(accountNumber, new BigDecimal(15000), epochMillisAtWarsawOffset("2023-02-10T11:00:00")),
                            new AccountDebitedEvent(accountNumber, new BigDecimal(3500), epochMillisAtWarsawOffset("2023-02-11T15:00:00")),
                            new AccountDebitedEvent(accountNumber, new BigDecimal(2000), epochMillisAtWarsawOffset("2023-02-20T22:00:00"))]
        transactions.each { eventGateway.publish(it) }

        then: 'they are visible in the from-the-beginning query result'
        with(queryGateway.query(allTransactionsQuery, TransactionsQueryResponse)) {
            it.get().transactions() == [new Transaction(1673344800000, CREDIT, new BigDecimal(15000)),
                                        new Transaction(1673445600000, DEBIT, new BigDecimal(3500)),
                                        new Transaction(1674248400000, DEBIT, new BigDecimal(2000)),
                                        new Transaction(1676023200000, CREDIT, new BigDecimal(15000)),
                                        new Transaction(1676124000000, DEBIT, new BigDecimal(3500)),
                                        new Transaction(1676926800000, DEBIT, new BigDecimal(2000))]
        }

        then: "query for last month's transactions returns correct three transactions"
        def lastMonthQuery = new AccountTransactionsQuery(accountNumber, epochMillisAtWarsawOffset("2023-02-01T00:00"))
        with(queryGateway.query(lastMonthQuery, TransactionsQueryResponse)) {
            it.get().transactions() == [new Transaction(1676023200000, CREDIT, new BigDecimal(15000)),
                                        new Transaction(1676124000000, DEBIT, new BigDecimal(3500)),
                                        new Transaction(1676926800000, DEBIT, new BigDecimal(2000))]
        }

        then: "query for last two weeks' transactions returns single correct transaction"
        def lastTwoWeeksQuery = new AccountTransactionsQuery(accountNumber, epochMillisAtWarsawOffset("2023-02-15T00:00"))
        with(queryGateway.query(lastTwoWeeksQuery, TransactionsQueryResponse)) {
            it.get().transactions() == [new Transaction(1676926800000, DEBIT, new BigDecimal(2000))]
        }

        when: 'new transaction is inserted into log'
        def newTransaction = new AccountCreditedEvent(accountNumber, new BigDecimal(222), epochMillisAtWarsawOffset("2023-02-16T11:00:00"))
        eventGateway.publish(newTransaction)

        then: 'it is visible in query result'
        sleep(2000)
        with(queryGateway.query(lastTwoWeeksQuery, TransactionsQueryResponse)) {
            it.get().transactions().size() == 2
            it.get().transactions().contains(new Transaction(1676541600000, CREDIT, new BigDecimal(222)))
        }
    }

    long epochMillisAtWarsawOffset(String dateTime) {
        LocalDateTime.parse(dateTime).toInstant(warsawOffset).toEpochMilli()
    }
}
