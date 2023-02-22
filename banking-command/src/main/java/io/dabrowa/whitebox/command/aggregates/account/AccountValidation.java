package io.dabrowa.whitebox.command.aggregates.account;

import java.math.BigDecimal;

public class AccountValidation {
    public static class AccountValidationException extends Exception {
        public AccountValidationException(String message) {
            super(message);
        }
    }

    public void validateInitialBalance(final BigDecimal initialBalance) throws AccountValidationException {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountValidationException("Account's initial balance cannot be negative");
        }
    }

    public void validateOverdraftLimit(final BigDecimal overdraftLimit) throws AccountValidationException {
        if (overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountValidationException("Account's overdraft limit cannot be negative");
        }
    }

    public void validateDebitOperation(final BigDecimal balance, final BigDecimal overdraftLimit, final BigDecimal debitValue) throws AccountAggregate.InsufficientFundsException {
        var postTransactionBalance = balance.subtract(debitValue);
        var lowestAllowedBalance = overdraftLimit.negate();
        if (postTransactionBalance.compareTo(lowestAllowedBalance) < 0) {
            throw new AccountAggregate.InsufficientFundsException();
        }
    }
}
