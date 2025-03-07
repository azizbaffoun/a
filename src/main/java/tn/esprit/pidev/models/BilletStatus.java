package tn.esprit.pidev.models;

public enum BilletStatus {
    VALIDE("Valide"),
    ANNULE("Annulé"),
    NON_VALIDE("Non valide");

    private final String displayName;

    BilletStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static BilletStatus fromDisplayName(String displayName) {
        for (BilletStatus status : values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No BilletStatus with display name: " + displayName);
    }
} 