package tn.esprit.pidev.enums;

public enum EmpruntStatus {
    EMPRUNTE("Emprunté"),
    RETOURNE("Retourné"),
    EN_RETARD("En retard");
    
    private final String value;
    
    EmpruntStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 