package io.dabrowa.whitebox.command.aggregates.account;

/**
 * Ugly hack - see ADR entry "Central static account number generator"
 * TODO: Introduce proper design for (testable) unique account number generation
 */
public class AccountNumberService {
    private static AccountNumberRegistry INSTANCE;

    public static void set(final AccountNumberRegistry accountNumberRegistry) {
        AccountNumberService.INSTANCE = accountNumberRegistry;
    }

    public static AccountNumberRegistry get() {
        return INSTANCE;
    }
}
