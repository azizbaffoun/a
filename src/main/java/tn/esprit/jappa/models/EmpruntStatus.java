package tn.esprit.jappa.models;

public enum EmpruntStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    RETURNED("Returned"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled");

    private final String displayName;

    EmpruntStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 