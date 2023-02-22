package io.dabrowa.whitebox.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE, classes = [Main.class, SpringAppConfiguration.class])
class RedisBalanceRepositoryTest extends Specification {

    RedisBalanceRepository sut

    @Autowired
    TestAccountNumberProvider numberProvider

    @Autowired
    JedisPool jedisPool

    def setup() {
        try(Jedis jedis = jedisPool.resource) {
            jedis.keys("*").each { jedis.del(it) }
        }
        sut = new RedisBalanceRepository(jedisPool)
    }

    def "returns empty response for account not in cache"() {
        given:
        def accountNumber = numberProvider.nextAvailable

        expect:
        sut.getBalance(accountNumber).empty
    }

    def "returns response for account in cache"() {
        given:
        def accountNumber = numberProvider.nextAvailable

        when:
        sut.credit(accountNumber, new BigDecimal("1245.965"))

        then:
        with(sut.getBalance(accountNumber)) {
            it.present
            it.get() == new BigDecimal("1245.965")
        }
    }

    def "account transactions are correctly reflected in cache"() {
        given:
        def accountNumber = numberProvider.nextAvailable

        when:
        sut.credit(accountNumber, new BigDecimal("120.00"))
        sut.credit(accountNumber, new BigDecimal("30.00"))
        sut.debit(accountNumber, new BigDecimal("90.00"))

        then:
        with(sut.getBalance(accountNumber)) {
            it.present
            it.get() == new BigDecimal("60")
        }

        when:
        sut.debit(accountNumber, new BigDecimal("15.15"))
        sut.credit(accountNumber, new BigDecimal("2.88"))

        then:
        with(sut.getBalance(accountNumber)) {
            it.present
            it.get() == new BigDecimal("47.73")
        }
    }

    def "accounts with negative balances are correctly identified"() {
        given:
        def accountNumber = numberProvider.nextAvailable
        def otherAccountNumber = numberProvider.nextAvailable

        when: 'accounts have positive balances'
        sut.credit(accountNumber, new BigDecimal("120.00"))
        sut.credit(otherAccountNumber, new BigDecimal("30.00"))

        then: 'response is empty'
        sut.accountsWithNegativeBalance().empty

        when: 'one account goes below zero'
        sut.debit(otherAccountNumber, new BigDecimal("130.00"))

        then: 'it is returned in response'
        sut.accountsWithNegativeBalance().size() == 1
        sut.accountsWithNegativeBalance() == [otherAccountNumber] as Set

        when: 'second account goes below zero'
        sut.debit(accountNumber, new BigDecimal("220.00"))

        then: 'they are both on the result list'
        sut.accountsWithNegativeBalance().size() == 2
        sut.accountsWithNegativeBalance() == [accountNumber, otherAccountNumber] as Set

        when: 'account pays the debt'
        sut.credit(otherAccountNumber, new BigDecimal("1000.00"))

        then: 'it is no longer on the offender list'
        sut.accountsWithNegativeBalance().size() == 1
        sut.accountsWithNegativeBalance() == [accountNumber] as Set

        when: 'second account pays the debt'
        sut.credit(accountNumber, new BigDecimal("1000.00"))

        then: 'it too is no longer on the offender list'
        sut.accountsWithNegativeBalance().empty
    }
}
