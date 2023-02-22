package io.dabrowa.whitebox.api.commands;

public record CreateAccountCommand(long initialBalance, long overdraftLimit) {
}
