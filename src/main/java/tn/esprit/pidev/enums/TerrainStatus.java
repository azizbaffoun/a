package tn.esprit.pidev.enums;

public enum TerrainStatus {
    DISPONIBLE("Disponible"),
    RESERVE("Réservé"),
    MAINTENANCE("Maintenance");
    
    private final String value;
    
    TerrainStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TerrainStatus fromString(String text) {
        for (TerrainStatus status : TerrainStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 