package io.dabrowa.whitebox.api.queries;

import java.util.Set;

public record AccountsInTheRedQuery() {
    public record Response(Set<String> accountNumbers) {
    }
}
