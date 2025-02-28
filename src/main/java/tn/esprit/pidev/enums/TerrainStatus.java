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
} 