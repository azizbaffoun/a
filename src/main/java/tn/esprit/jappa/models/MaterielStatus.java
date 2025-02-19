package tn.esprit.jappa.models;

public enum MaterielStatus {
    AVAILABLE("Available"),
    RESERVED("Reserved"),
    IN_MAINTENANCE("In Maintenance"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    MaterielStatus(String displayName) {
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