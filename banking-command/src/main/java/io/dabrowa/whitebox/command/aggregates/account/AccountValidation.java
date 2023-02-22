package io.dabrowa.whitebox.command.aggregates.account;

public class AccountValidation {
    public static class AccountValidationException extends Exception {
        public AccountValidationException(String message) {
            super(message);
        }
    }

    public void validateInitialBalance(final long initialBalance) throws AccountValidationException {
        if(initialBalance < 0) {
            throw new AccountValidationException("Account's initial balance cannot be negative");
        }
    }

    public void validateOverdraftLimit(final long overdraftLimit) throws AccountValidationException {
        if(overdraftLimit < 0) {
            throw new AccountValidationException("Account's overdraft limit cannot be negative");
        }
    }
}
