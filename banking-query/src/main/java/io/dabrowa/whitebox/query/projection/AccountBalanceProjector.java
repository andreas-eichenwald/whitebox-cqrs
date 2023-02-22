package io.dabrowa.whitebox.query.projection;

import io.dabrowa.whitebox.query.repository.BalanceRepository;

public class AccountBalanceProjector {

    private final BalanceRepository balanceRepository;

    public AccountBalanceProjector(final BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }
}
