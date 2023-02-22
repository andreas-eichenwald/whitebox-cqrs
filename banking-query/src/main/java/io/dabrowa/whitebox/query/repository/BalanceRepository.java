package io.dabrowa.whitebox.query.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface BalanceRepository {
    Optional<BigDecimal> getBalance(String accountNumber);

    void debit(String accountNumber, BigDecimal value);

    void credit(String accountNumber, BigDecimal value);

    Set<String> accountsWithNegativeBalance();

    /**
     * Decorator pattern-style class encapsulating logic potentially useful
     * for all implementations: only allow positive values for credit
     * and debits, as they should always be positive and their type
     * is determined by context (credit/debit operation)
     */
    class NonNegativeValuesRepository implements BalanceRepository {
        private final BalanceRepository delegate;

        public NonNegativeValuesRepository(final BalanceRepository delegate) {
            this.delegate = delegate;
        }

        @Override
        public Optional<BigDecimal> getBalance(final String accountNumber) {
            return delegate.getBalance(accountNumber);
        }

        @Override
        public void debit(final String accountNumber, final BigDecimal value) {
            ensureNonNegative(value);
            delegate.debit(accountNumber, value);
        }

        @Override
        public void credit(final String accountNumber, final BigDecimal value) {
            ensureNonNegative(value);
            delegate.credit(accountNumber, value);
        }

        @Override
        public Set<String> accountsWithNegativeBalance() {
            return delegate.accountsWithNegativeBalance();
        }

        private void ensureNonNegative(BigDecimal value) {
            if(value.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Value cannot be negative");
            }
        }
    }
}
