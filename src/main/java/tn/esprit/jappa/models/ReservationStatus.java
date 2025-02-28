package tn.esprit.jappa.models;

public enum ReservationStatus {
    EN_ATTENTE("En attente"),
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

    public static ReservationStatus fromString(String text) {
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 