package io.dabrowa.whitebox.query.handling;

import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery;
import io.dabrowa.whitebox.api.queries.AccountTransactionsQuery.TransactionsQueryResponse;
import io.dabrowa.whitebox.query.repository.TransactionRepository;
import org.axonframework.queryhandling.QueryHandler;

public class TransactionsQueryHandler {
    private final TransactionRepository transactionRepository;

    public TransactionsQueryHandler(final TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @QueryHandler
    public TransactionsQueryResponse transactions(final AccountTransactionsQuery query) {
        return new TransactionsQueryResponse(transactionRepository.fetchLaterThan(
                query.accountNumber(),
                query.epochMillis()
        ));
    }
}
