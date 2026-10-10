package com.synapsecalyx.sentrax.model;

import java.time.LocalDateTime;

/** An alert raised when a transaction trips one fraud rule. */
public class Alert {
    private long id;
    private long transactionId;
    private String ruleName;
    private String message;
    private LocalDateTime createdAt;
    private boolean resolved;
    private String resolvedBy;
    private LocalDateTime resolvedAt;

    public Alert() {
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    public Alert(long transactionId, String ruleName, String message) {
        this();
        this.transactionId = transactionId;
        this.ruleName = ruleName;
        this.message = message;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
