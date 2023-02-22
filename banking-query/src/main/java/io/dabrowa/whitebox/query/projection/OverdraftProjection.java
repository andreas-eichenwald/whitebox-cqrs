package io.dabrowa.whitebox.query.projection;

import io.dabrowa.whitebox.api.events.AccountCreatedEvent;
import io.dabrowa.whitebox.query.repository.OverdraftLimitRepository;
import org.axonframework.eventsourcing.EventSourcingHandler;

public class OverdraftProjection {
    private final OverdraftLimitRepository repository;

    public OverdraftProjection(final OverdraftLimitRepository repository) {
        this.repository = repository;
    }

    @EventSourcingHandler
    public void on(final AccountCreatedEvent event) {
        repository.saveOverdraftLimit(event.accountId(), event.overdraftLimit());
    }
}
