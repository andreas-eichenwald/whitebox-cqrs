package io.dabrowa.whitebox.query.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

public class InMemoryBalanceRepository implements BalanceRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryBalanceRepository.class);

    private final Map<String, BigDecimal> accountBalances = new ConcurrentHashMap<>();

    @Override
    public Optional<BigDecimal> getBalance(final String accountNumber) {
        if (accountBalances.containsKey(accountNumber)) {
            return Optional.of(accountBalances.get(accountNumber));
        }
        return Optional.empty();
    }

    @Override
    public void debit(final String accountNumber, final BigDecimal value) {
        LOGGER.debug("Debiting account {} for {}", accountNumber, value);
        accountBalances.compute(accountNumber, (_accountNumber, oldBalance) -> {
            if (oldBalance == null) {
                return value.negate();
            } else {
                return oldBalance.subtract(value);
            }
        });
    }

    @Override
    public void credit(final String accountNumber, final BigDecimal value) {
        LOGGER.debug("Crediting account {} for {}", accountNumber, value);
        accountBalances.compute(accountNumber, (_accountNumber, oldBalance) -> {
            if (oldBalance == null) {
                return value;
            } else {
                return oldBalance.add(value);
            }
        });
    }

    @Override
    public List<String> accountsWithNegativeBalance() {
        return accountBalances.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) < 0)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    public void cleanup() {
        this.accountBalances.clear();
    }
}
