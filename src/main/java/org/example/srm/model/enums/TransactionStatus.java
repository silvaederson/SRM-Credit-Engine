package org.example.srm.model.enums;

public enum TransactionStatus {
    PENDING("Pending"),
    SETTLED("Settled"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    UNDER_REVIEW("Under Review");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}