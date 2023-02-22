package io.dabrowa.whitebox.domain.commands;

public record CreateAccountCommand(long initialBalance, long overdraftLimit) {
}
