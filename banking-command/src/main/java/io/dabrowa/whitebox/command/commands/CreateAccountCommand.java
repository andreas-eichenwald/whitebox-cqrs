package io.dabrowa.whitebox.command.commands;

public record CreateAccountCommand(long initialBalance, long overdraftLimit) {
}
