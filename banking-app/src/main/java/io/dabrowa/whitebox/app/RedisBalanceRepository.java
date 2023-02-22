package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.query.repository.BalanceRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RedisBalanceRepository implements BalanceRepository {

    private static final String BALANCE_KEY_PREFIX = "account-balance";
    private static final String NEGATIVE_BALANCE_ACCOUNTS_KEY = "negative-balances";

    private final JedisPool jedisPool;

    public RedisBalanceRepository(final JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public Optional<BigDecimal> getBalance(String accountNumber) {
        try (final Jedis jedis = jedisPool.getResource()) {
            final var balance = jedis.get(key(accountNumber));
            return Optional.ofNullable(balance)
                    .map(BigDecimal::new);
        }
    }

    @Override
    public void debit(String accountNumber, BigDecimal value) {
        try (final Jedis jedis = jedisPool.getResource()) {
            final var key = key(accountNumber);

            jedis.watch(key);
            final var string = jedis.get(key);

            BigDecimal newBalance = newDebitValue(value, string);
            final Transaction transaction = jedis.multi();
            transaction.set(key, newBalance.toString());
            ensureNoConcurrentModification(transaction);

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                jedis.sadd(NEGATIVE_BALANCE_ACCOUNTS_KEY, accountNumber);
            }
        }
    }

    @Override
    public void credit(String accountNumber, BigDecimal value) {
        try (final Jedis jedis = jedisPool.getResource()) {
            final var key = key(accountNumber);

            jedis.watch(key);
            final var string = jedis.get(key);

            BigDecimal newBalance = newCreditValue(value, string);
            final Transaction transaction = jedis.multi();
            transaction.set(key, newBalance.toString());
            ensureNoConcurrentModification(transaction);

            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                jedis.srem(NEGATIVE_BALANCE_ACCOUNTS_KEY, accountNumber);
            }
        }
    }

    /**
     * Since balance operations are of type read-then-write, there is risk
     * of concurrency issues - after the read, but before write the value could
     * have been modified, leading to lost updates and incorrect values.
     * Watch is used to prevent this - if the key was modified since it had been
     * read, then write transaction will fail.
     * There could be more sophisticated retry strategy in place,
     * but this is used in idempotent event handlers, retries can be
     * delegated to the framework. Besides, this is just to showcase the concept.
     */
    private void ensureNoConcurrentModification(final Transaction transaction) {
        final var transactionResult = transaction.exec();
        if (transactionResult == null) {
            throw new ConcurrentModificationException();
        }
    }

    @Override
    public Set<String> accountsWithNegativeBalance() {
        try (final Jedis jedis = jedisPool.getResource()) {
            return Set.copyOf(jedis.smembers(NEGATIVE_BALANCE_ACCOUNTS_KEY));
        }
    }

    private BigDecimal newCreditValue(BigDecimal value, String string) {
        if (string == null) {
            return value;
        } else {
            return new BigDecimal(string).add(value);
        }
    }

    private BigDecimal newDebitValue(BigDecimal value, String string) {
        if (string == null) {
            return value.negate();
        } else {
            return new BigDecimal(string).subtract(value);
        }
    }

    private String key(final String accountNumber) {
        return "%s-%s".formatted(BALANCE_KEY_PREFIX, accountNumber);
    }
}
