package io.dabrowa.whitebox.query.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

public class InMemoryBalanceRepository implements BalanceRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryBalanceRepository.class);

    private final Map<String, Long> accountBalances = new ConcurrentHashMap<>();

    @Override
    public Optional<Long> getBalance(final String accountNumber) {
        if (accountBalances.containsKey(accountNumber)) {
            return Optional.of(accountBalances.get(accountNumber));
        }
        return Optional.empty();
    }

    @Override
    public void debit(final String accountNumber, final long value) {
        LOGGER.debug("Debiting account {} for {}", accountNumber, value);
        accountBalances.compute(accountNumber, (_accountNumber, oldBalance) -> {
            if (oldBalance == null) {
                return -value;
            } else {
                return oldBalance - value;
            }
        });
    }

    @Override
    public void credit(final String accountNumber, final long value) {
        LOGGER.debug("Crediting account {} for {}", accountNumber, value);
        accountBalances.compute(accountNumber, (_accountNumber, oldBalance) -> {
            if (oldBalance == null) {
                return value;
            } else {
                return oldBalance + value;
            }
        });
    }

    @Override
    public List<String> accountsWithNegativeBalance() {
        return accountBalances.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    void cleanup() {
        this.accountBalances.clear();
    }
}
