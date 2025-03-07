package tn.esprit.pidev.models;

public enum EvenementStatus {
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    ANNULE("Annulé");

    private final String displayName;

    EvenementStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static EvenementStatus fromDisplayName(String displayName) {
        for (EvenementStatus status : values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No EvenementStatus with display name: " + displayName);
    }
} 