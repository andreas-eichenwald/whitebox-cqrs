package io.dabrowa.whitebox.query.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryLimitRepository implements OverdraftLimitRepository {

    private final Map<String, BigDecimal> perAccountLimits;

    public InMemoryLimitRepository() {
        this.perAccountLimits = new HashMap<>();
    }

    @Override
    public Optional<BigDecimal> overdraftLimitFor(final String accountNumber) {
        if (perAccountLimits.containsKey(accountNumber)) {
            return Optional.of(perAccountLimits.get(accountNumber));
        }
        return Optional.empty();
    }

    @Override
    public void saveOverdraftLimit(final String accountNumber, final BigDecimal overdraftLimit) {
        perAccountLimits.put(accountNumber, overdraftLimit);
    }
}
