package io.dabrowa.whitebox.api.commands;

import java.math.BigDecimal;

public record CreateAccountCommand(String number, BigDecimal initialBalance, BigDecimal overdraftLimit) {
}
