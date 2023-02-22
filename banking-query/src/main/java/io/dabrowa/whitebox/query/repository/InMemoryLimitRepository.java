package io.dabrowa.whitebox.query.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryLimitRepository implements OverdraftLimitRepository {

    private final Map<String, Long> perAccountLimits;

    public InMemoryLimitRepository() {
        this.perAccountLimits = new HashMap<>();
    }

    @Override
    public Optional<Long> overdraftLimitFor(final String accountNumber) {
        if (perAccountLimits.containsKey(accountNumber)) {
            return Optional.of(perAccountLimits.get(accountNumber));
        }
        return Optional.empty();
    }

    @Override
    public void saveOverdraftLimit(final String accountNumber, final long overdraftLimit) {
        perAccountLimits.put(accountNumber, overdraftLimit);
    }
}
