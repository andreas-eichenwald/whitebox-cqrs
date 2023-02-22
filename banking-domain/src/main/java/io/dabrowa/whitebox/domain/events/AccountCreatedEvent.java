package io.dabrowa.whitebox.domain.events;

public record AccountCreatedEvent(String accountId, long initialBalance, long overdraftLimit) implements Event {
}
