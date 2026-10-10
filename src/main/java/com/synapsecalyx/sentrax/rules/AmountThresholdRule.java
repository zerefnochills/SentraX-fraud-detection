package com.synapsecalyx.sentrax.rules;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.Transaction;

import java.util.List;

/**
 * Flags transactions that are unusually large compared to the
 * customer's own recent average spend, rather than a single fixed
 * number — a customer who normally spends 500 gets flagged at 20,000
 * while a customer who normally spends 50,000 does not.
 */
public class AmountThresholdRule extends AbstractFraudRule {

    private static final double HARD_CEILING = 200_000.0;

    public AmountThresholdRule() {
        super("Amount Threshold", 0.30);
    }

    @Override
    public double evaluate(Transaction transaction, Customer customer, List<Transaction> recentTransactions) {
        if (transaction.getAmount() >= HARD_CEILING) {
            return 100.0;
        }

        double averageSpend = recentTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .average()
                .orElse(transaction.getAmount());

        if (averageSpend <= 0) {
            return 0.0;
        }

        double ratio = transaction.getAmount() / averageSpend;
        if (ratio <= 2.0) {
            return 0.0;
        }
        double score = ((ratio - 2.0) / 8.0) * 100.0; // scale 2x-10x average into 0-100
        return Math.min(score, 100.0);
    }

    @Override
    public String describeViolation(Transaction transaction) {
        return String.format("Amount %.2f is far above this customer's typical spend.", transaction.getAmount());
    }
}
