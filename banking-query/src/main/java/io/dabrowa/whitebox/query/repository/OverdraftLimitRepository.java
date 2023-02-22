package io.dabrowa.whitebox.query.repository;

import java.math.BigDecimal;
import java.util.Optional;

public interface OverdraftLimitRepository {
    Optional<BigDecimal> overdraftLimitFor(String accountNumber);

    void saveOverdraftLimit(String accountNumber, BigDecimal overdraftLimit);
}
