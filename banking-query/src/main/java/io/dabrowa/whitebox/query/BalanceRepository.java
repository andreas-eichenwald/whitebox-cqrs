package io.dabrowa.whitebox.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BalanceRepository {
    private final Map<String, Long> accountBalances = new HashMap<>();

    public Optional<Long> getBalance(final String accountNumber) {
        if(accountBalances.containsKey(accountNumber)) {
            return Optional.of(accountBalances.get(accountNumber));
        }
        return Optional.empty();
    }

    public void updateBalance(final String accountNumber, final long balance) {
        accountBalances.put(accountNumber, balance);
    }
}
