package tn.esprit.pidev.enums;

public enum MaintenanceStatus {
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    PLANIFIE("Planifié");
    
    private final String value;
    
    MaintenanceStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 