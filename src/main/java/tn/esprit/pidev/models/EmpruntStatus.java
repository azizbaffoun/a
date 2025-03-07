package tn.esprit.pidev.models;

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

    public static EmpruntStatus fromDisplayName(String displayName) {
        for (EmpruntStatus status : values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No EmpruntStatus with display name: " + displayName);
    }
} 