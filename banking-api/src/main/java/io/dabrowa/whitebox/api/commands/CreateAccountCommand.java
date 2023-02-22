package io.dabrowa.whitebox.api.commands;

public record CreateAccountCommand(String number, long initialBalance, long overdraftLimit) {
}
