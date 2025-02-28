package tn.esprit.pidev.enums;

public enum ReservationStatus {
    CONFIRMEE("Confirmée"),
    ANNULEE("Annulée");
    
    private final String value;
    
    ReservationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 