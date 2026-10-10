package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.model.Transaction;

import java.util.List;

public interface TransactionDAO extends GenericDAO<Transaction, Long> {

    /** Most recent transactions for one customer, newest first, used by the rule engine. */
    List<Transaction> findRecentByCustomer(long customerId, int limit);

    /** All transactions currently flagged as fraud, for the alerts dashboard. */
    List<Transaction> findFlagged();
}
