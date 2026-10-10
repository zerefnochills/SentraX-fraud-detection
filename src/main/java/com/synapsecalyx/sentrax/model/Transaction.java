package com.synapsecalyx.sentrax.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A single financial transaction submitted for fraud evaluation.
 * Implements {@link Comparable} so lists of transactions can be sorted
 * by risk score (highest risk first) with no extra comparator class.
 */
public class Transaction implements Comparable<Transaction> {

    public enum Status { PENDING, APPROVED, FLAGGED }

    private long id;
    private long customerId;
    private double amount;
    private String currency;
    private String merchant;
    private String location;
    private String deviceId;
    private LocalDateTime timestamp;
    private Status status;
    private double riskScore;
    private RiskLevel riskLevel;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = Status.PENDING;
        this.riskLevel = RiskLevel.SAFE;
    }

    public Transaction(long customerId, double amount, String currency, String merchant,
                        String location, String deviceId, LocalDateTime timestamp) {
        this();
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.merchant = merchant;
        this.location = location;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    @Override
    public int compareTo(Transaction other) {
        return Double.compare(other.riskScore, this.riskScore); // descending: highest risk first
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", amount=" + amount + ", riskLevel=" + riskLevel + "}";
    }
}
