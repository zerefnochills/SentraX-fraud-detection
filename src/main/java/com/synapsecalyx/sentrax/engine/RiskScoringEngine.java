package com.synapsecalyx.sentrax.engine;

import com.synapsecalyx.sentrax.exception.FraudDetectionException;
import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.RiskLevel;
import com.synapsecalyx.sentrax.model.Transaction;
import com.synapsecalyx.sentrax.rules.AmountThresholdRule;
import com.synapsecalyx.sentrax.rules.FraudRule;
import com.synapsecalyx.sentrax.rules.LocationMismatchRule;
import com.synapsecalyx.sentrax.rules.NewDeviceRule;
import com.synapsecalyx.sentrax.rules.OddHourRule;
import com.synapsecalyx.sentrax.rules.VelocityRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Combines every registered {@link FraudRule} into a single weighted
 * risk score for a transaction. New rules are added by appending to the
 * list in the constructor — this class never needs to change, which is
 * the Open/Closed half of good OOP design; polymorphism does the rest,
 * since every rule is called through the same {@code FraudRule}
 * interface regardless of its concrete type.
 */
public class RiskScoringEngine {

    private final List<FraudRule> rules;

    public RiskScoringEngine() {
        this.rules = new ArrayList<>();
        rules.add(new AmountThresholdRule());
        rules.add(new VelocityRule());
        rules.add(new LocationMismatchRule());
        rules.add(new OddHourRule());
        rules.add(new NewDeviceRule());
    }

    /** Lets tests or callers supply a custom rule set. */
    public RiskScoringEngine(List<FraudRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    /**
     * Scores the transaction and returns the list of rules that
     * contributed a non-trivial amount of risk, so the caller can turn
     * each one into an {@code Alert}.
     */
    public ScoringResult score(Transaction transaction, Customer customer, List<Transaction> recentTransactions) {
        if (transaction == null || customer == null) {
            throw new FraudDetectionException("Transaction and customer must not be null.");
        }

        double weightedTotal = 0.0;
        List<FraudRule> triggered = new ArrayList<>();

        for (FraudRule rule : rules) {
            double ruleScore;
            try {
                ruleScore = rule.evaluate(transaction, customer, recentTransactions);
            } catch (RuntimeException ex) {
                throw new FraudDetectionException("Rule '" + rule.getName() + "' failed to evaluate.", ex);
            }
            weightedTotal += ruleScore * rule.getWeight();
            if (ruleScore >= 40.0) {
                triggered.add(rule);
            }
        }

        double finalScore = Math.min(weightedTotal, 100.0);
        RiskLevel level = RiskLevel.fromScore(finalScore);

        return new ScoringResult(finalScore, level, Collections.unmodifiableList(triggered));
    }

    /** Immutable result of a scoring pass: score, derived level, and which rules fired. */
    public static final class ScoringResult {
        private final double score;
        private final RiskLevel level;
        private final List<FraudRule> triggeredRules;

        public ScoringResult(double score, RiskLevel level, List<FraudRule> triggeredRules) {
            this.score = score;
            this.level = level;
            this.triggeredRules = triggeredRules;
        }

        public double getScore() { return score; }
        public RiskLevel getLevel() { return level; }
        public List<FraudRule> getTriggeredRules() { return triggeredRules; }
    }
}
