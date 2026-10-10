package com.synapsecalyx.sentrax.rules;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.Transaction;

import java.util.List;

/**
 * Flags a transaction made from a location far from the customer's home
 * location or last known location, with no reasonable travel gap.
 */
public class LocationMismatchRule extends AbstractFraudRule {

    public LocationMismatchRule() {
        super("Location Mismatch", 0.20);
    }

    @Override
    public double evaluate(Transaction transaction, Customer customer, List<Transaction> recentTransactions) {
        if (transaction.getLocation() == null || customer.getHomeLocation() == null) {
            return 0.0;
        }

        if (transaction.getLocation().equalsIgnoreCase(customer.getHomeLocation())) {
            return 0.0;
        }

        String lastLocation = recentTransactions.isEmpty()
                ? customer.getHomeLocation()
                : recentTransactions.get(0).getLocation();

        if (transaction.getLocation().equalsIgnoreCase(lastLocation)) {
            return 15.0; // consistent with recent travel, mild score only
        }

        return 60.0; // sudden, unexplained change of location
    }

    @Override
    public String describeViolation(Transaction transaction) {
        return "Transaction location (" + transaction.getLocation() + ") does not match the customer's usual pattern.";
    }
}
