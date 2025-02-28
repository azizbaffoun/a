package tn.esprit.pidev.enums;

public enum MaterielStatus {
    DISPONIBLE("Disponible"),
    RESERVE("Réservé"),
    SOUS_MAINTENANCE("Sous maintenance");
    
    private final String value;
    
    MaterielStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 