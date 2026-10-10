package com.synapsecalyx.sentrax.rules;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.Transaction;

import java.util.List;

/** Flags a transaction made from a device never before used by this customer. */
public class NewDeviceRule extends AbstractFraudRule {

    public NewDeviceRule() {
        super("New Device", 0.15);
    }

    @Override
    public double evaluate(Transaction transaction, Customer customer, List<Transaction> recentTransactions) {
        if (transaction.getDeviceId() == null) {
            return 0.0;
        }
        if (transaction.getDeviceId().equals(customer.getRegisteredDeviceId())) {
            return 0.0;
        }
        boolean seenBefore = recentTransactions.stream()
                .anyMatch(t -> transaction.getDeviceId().equals(t.getDeviceId()));
        return seenBefore ? 10.0 : 50.0;
    }

    @Override
    public String describeViolation(Transaction transaction) {
        return "Transaction was made from a device not previously associated with this customer.";
    }
}
