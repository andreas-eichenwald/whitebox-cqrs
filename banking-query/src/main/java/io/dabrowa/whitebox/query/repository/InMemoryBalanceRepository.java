package io.dabrowa.whitebox.query.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBalanceRepository implements BalanceRepository {
    private final Map<String, Long> accountBalances = new HashMap<>();

    @Override
    public Optional<Long> getBalance(final String accountNumber) {
        if(accountBalances.containsKey(accountNumber)) {
            return Optional.of(accountBalances.get(accountNumber));
        }
        return Optional.empty();
    }

    @Override
    public void updateBalance(final String accountNumber, final long balance) {
        accountBalances.put(accountNumber, balance);
    }
}
