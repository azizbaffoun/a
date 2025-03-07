package tn.esprit.pidev.models;

public enum MaintenanceStatus {
    SCHEDULED("Scheduled"),
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    MaintenanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return name();
    }

    public static MaintenanceStatus fromDisplayName(String displayName) {
        for (MaintenanceStatus status : values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No MaintenanceStatus with display name: " + displayName);
    }
} 