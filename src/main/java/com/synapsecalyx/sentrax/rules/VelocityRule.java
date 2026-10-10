package com.synapsecalyx.sentrax.rules;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.Transaction;

import java.time.Duration;
import java.util.List;

/**
 * Flags a burst of transactions in a short window — a classic sign of a
 * stolen card being used rapidly before it gets blocked.
 */
public class VelocityRule extends AbstractFraudRule {

    private static final Duration WINDOW = Duration.ofMinutes(10);
    private static final int SAFE_COUNT = 2;

    public VelocityRule() {
        super("Transaction Velocity", 0.25);
    }

    @Override
    public double evaluate(Transaction transaction, Customer customer, List<Transaction> recentTransactions) {
        long countInWindow = recentTransactions.stream()
                .filter(t -> Duration.between(t.getTimestamp(), transaction.getTimestamp()).abs().compareTo(WINDOW) <= 0)
                .count();

        if (countInWindow <= SAFE_COUNT) {
            return 0.0;
        }
        double score = (countInWindow - SAFE_COUNT) * 25.0;
        return Math.min(score, 100.0);
    }

    @Override
    public String describeViolation(Transaction transaction) {
        return "Multiple transactions were made within a 10-minute window.";
    }
}
