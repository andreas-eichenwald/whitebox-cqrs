package io.dabrowa.whitebox.command.aggregates.account;

public interface AccountNumberRegistry {

    /**
     * Generate and obtain a lock on the "next" available account number.
     * "next" is defined in some sequential way that is implementation specific.
     * Once this method returns the lock on returned account number has been obtained
     * and no other command handler can retrieve it, thus this account number is guaranteed
     * to be unique across the system and safe to use in the event.
     */
    String getNextAvailable();
}
