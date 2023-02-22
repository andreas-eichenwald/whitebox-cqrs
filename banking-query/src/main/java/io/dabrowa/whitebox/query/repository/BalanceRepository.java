package io.dabrowa.whitebox.query.repository;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository {
    Optional<Long> getBalance(String accountNumber);

    void debit(String accountNumber, long value);

    void credit(String accountNumber, long value);

    List<String> accountsWithNegativeBalance();
}
