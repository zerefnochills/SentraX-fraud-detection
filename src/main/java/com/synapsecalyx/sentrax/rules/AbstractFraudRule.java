package com.synapsecalyx.sentrax.rules;

/**
 * Base class that stores a rule's name and weight so concrete rules only
 * need to implement {@code evaluate} and {@code describeViolation}.
 * Demonstrates abstraction + inheritance layered on top of the
 * {@link FraudRule} interface.
 */
public abstract class AbstractFraudRule implements FraudRule {

    private final String name;
    private final double weight;

    protected AbstractFraudRule(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
