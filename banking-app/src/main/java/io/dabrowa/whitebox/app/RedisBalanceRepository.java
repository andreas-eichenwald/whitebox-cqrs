package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.query.repository.BalanceRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.math.BigDecimal;
import java.util.Collections;
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

            BigDecimal newBalance;
            if (string == null) {
                newBalance = value.negate();
            } else {
                newBalance = new BigDecimal(string).subtract(value);
            }
            final Transaction transaction = jedis.multi();
            transaction.set(key, newBalance.toString());
            transaction.exec();

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

            BigDecimal newBalance;
            if (string == null) {
                newBalance = value;
            } else {
                newBalance = new BigDecimal(string).add(value);
            }
            final Transaction transaction = jedis.multi();
            transaction.set(key, newBalance.toString());
            transaction.exec();

            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                jedis.srem(NEGATIVE_BALANCE_ACCOUNTS_KEY, accountNumber);
            }
        }
    }

    @Override
    public Set<String> accountsWithNegativeBalance() {
        try (final Jedis jedis = jedisPool.getResource()) {
            return Set.copyOf(jedis.smembers(NEGATIVE_BALANCE_ACCOUNTS_KEY));
        }
    }

    private String key(final String accountNumber) {
        return "%s-%s".formatted(BALANCE_KEY_PREFIX, accountNumber);
    }
}
