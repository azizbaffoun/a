package tn.esprit.pidev.models;

public enum TerrainStatus {
    DISPONIBLE("Disponible"),
    RESERVE("Réservé"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    TerrainStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static TerrainStatus fromDisplayName(String displayName) {
        for (TerrainStatus status : values()) {
            if (status.getDisplayName().equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No TerrainStatus with display name: " + displayName);
    }
} 