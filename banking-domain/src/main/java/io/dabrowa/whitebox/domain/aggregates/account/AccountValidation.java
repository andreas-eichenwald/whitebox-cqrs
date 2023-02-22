package io.dabrowa.whitebox.domain.aggregates.account;

import java.util.regex.Pattern;

public class AccountValidation {
    public static class AccountValidationException extends Exception {
        public AccountValidationException(String message) {
            super(message);
        }
    }

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\d\\d\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\d");

    public void validateNumber(final String accountNumberString) throws AccountValidationException {
        if (accountNumberInvalid(accountNumberString)) {
            throw new AccountValidationException("Invalid account number: " + accountNumberString);
        }
    }

    public void validateInitialBalance(final long initialBalance) throws AccountValidationException {
        if(initialBalance <= 0) {
            throw new AccountValidationException("Account's initial balance must be positive");
        }
    }

    public void validateOverdraftLimit(final long overdraftLimit) throws AccountValidationException {
        if(overdraftLimit <= 0) {
            throw new AccountValidationException("Account's overdraft limit must be positive");
        }
    }

    private boolean accountNumberInvalid(String accountNumberString) {
        return !ACCOUNT_NUMBER_PATTERN.matcher(accountNumberString).matches();
    }
}
