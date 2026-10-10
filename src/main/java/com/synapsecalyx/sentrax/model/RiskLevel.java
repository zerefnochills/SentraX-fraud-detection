package com.synapsecalyx.sentrax.model;

/**
 * Risk classification assigned to a transaction after it has been
 * evaluated by the {@link com.synapsecalyx.sentrax.engine.RiskScoringEngine}.
 */
public enum RiskLevel {
    SAFE(0, "Safe"),
    SUSPICIOUS(1, "Suspicious"),
    FRAUD(2, "Fraud");

    private final int severity;
    private final String label;

    RiskLevel(int severity, String label) {
        this.severity = severity;
        this.label = label;
    }

    public int getSeverity() {
        return severity;
    }

    public String getLabel() {
        return label;
    }

    /** Derives a risk level from a numeric score in the range 0-100. */
    public static RiskLevel fromScore(double score) {
        if (score >= 70) {
            return FRAUD;
        } else if (score >= 35) {
            return SUSPICIOUS;
        }
        return SAFE;
    }
}
