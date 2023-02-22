package io.dabrowa.whitebox.app;

import io.dabrowa.whitebox.command.aggregates.account.AccountNumberRegistry;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestAccountNumberProvider implements AccountNumberRegistry {

    // account numbers in form of xxxx-xxxx-xxxx-xxxx
    private static final int ACCOUNT_NUMBER_DIGITS = 4 * 4;

    private static final AtomicLong nextAccountNumber = new AtomicLong(0);

    @Override
    public String getNextAvailable() {
        final var nextLongId = nextAccountNumber.incrementAndGet();
        return asAccountNumber(nextLongId);
    }

    private String asAccountNumber(final long longId) {
        final var shortString = String.valueOf(longId);
        final var trailingZerosNumber = ACCOUNT_NUMBER_DIGITS - shortString.length();
        final var trailingZeros = Stream.generate(() -> "0")
                .limit(trailingZerosNumber)
                .collect(Collectors.joining());
        final var stringWithoutDashes = trailingZeros + shortString;
        return "%s-%s-%s-%s".formatted(
                stringWithoutDashes.substring(0, 4),
                stringWithoutDashes.substring(4, 8),
                stringWithoutDashes.substring(8, 12),
                stringWithoutDashes.substring(12, 16)
        );
    }
}
