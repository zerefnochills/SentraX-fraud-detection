package com.synapsecalyx.sentrax.rules;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.Transaction;

import java.util.List;

/**
 * Flags transactions made very late at night or very early morning — a
 * weak but useful signal when combined with other rules.
 */
public class OddHourRule extends AbstractFraudRule {

    public OddHourRule() {
        super("Odd Hour", 0.10);
    }

    @Override
    public double evaluate(Transaction transaction, Customer customer, List<Transaction> recentTransactions) {
        int hour = transaction.getTimestamp().getHour();
        if (hour >= 1 && hour <= 4) {
            return 40.0;
        }
        return 0.0;
    }

    @Override
    public String describeViolation(Transaction transaction) {
        return "Transaction occurred at an unusual hour (" + transaction.getTimestamp().toLocalTime() + ").";
    }
}
