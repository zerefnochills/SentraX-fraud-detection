package com.synapsecalyx.sentrax.rules;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.Transaction;

import java.util.List;

/**
 * A single fraud-detection rule. Every rule inspects one transaction
 * (plus the customer's recent history) and contributes a score between
 * 0 and 100 toward the transaction's overall risk. Implementations are
 * combined polymorphically by {@link com.synapsecalyx.sentrax.engine.RiskScoringEngine}
 * — new rules are added without changing the engine at all.
 */
public interface FraudRule {

    /**
     * @param transaction         the transaction being evaluated
     * @param customer            the customer who made it
     * @param recentTransactions  the customer's recent transaction history, most recent first
     * @return a score from 0 (no concern) to 100 (certain fraud)
     */
    double evaluate(Transaction transaction, Customer customer, List<Transaction> recentTransactions);

    /** Human-readable explanation shown in an alert when this rule fires. */
    String describeViolation(Transaction transaction);

    /** Relative importance of this rule's score in the weighted total. */
    double getWeight();

    String getName();
}
