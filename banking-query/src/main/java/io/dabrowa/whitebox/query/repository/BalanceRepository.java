package io.dabrowa.whitebox.query.repository;

import java.util.Optional;

public interface BalanceRepository {
    Optional<Long> getBalance(String accountNumber);

    void updateBalance(String accountNumber, long balance);
}
