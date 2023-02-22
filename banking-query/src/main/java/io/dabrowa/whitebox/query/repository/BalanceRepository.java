package io.dabrowa.whitebox.query.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BalanceRepository {
    Optional<BigDecimal> getBalance(String accountNumber);

    void debit(String accountNumber, BigDecimal value);

    void credit(String accountNumber, BigDecimal value);

    List<String> accountsWithNegativeBalance();
}
