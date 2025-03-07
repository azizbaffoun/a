package tn.esprit.pidev.models;

public enum ReservationStatus {
    CONFIRMEE("Confirmée"),
    ANNULEE("Annulée");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ReservationStatus fromDisplayName(String displayName) {
        for (ReservationStatus status : values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No ReservationStatus with display name: " + displayName);
    }
} 