package tn.esprit.pidev.enums;

public enum BilletStatus {
    VALIDE("Valide"),
    ANNULE("Annulé"),
    NON_VALIDE("Non valide");
    
    private final String value;
    
    BilletStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 