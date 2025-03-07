package tn.esprit.pidev.models;

public enum ReservationType {
    TERRAIN("TERRAIN"),
    BILLET("BILLET");

    private final String displayName;

    ReservationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ReservationType fromDisplayName(String displayName) {
        for (ReservationType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ReservationType with display name: " + displayName);
    }
} 