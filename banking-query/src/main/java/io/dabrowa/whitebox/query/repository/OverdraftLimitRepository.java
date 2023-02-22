package io.dabrowa.whitebox.query.repository;

import java.util.Optional;

public interface OverdraftLimitRepository {
    Optional<Long> overdraftLimitFor(String accountNumber);

    void saveOverdraftLimit(String accountNumber, long overdraftLimit);
}
