package tn.esprit.jappa.models;

public enum BilletStatus {
    VALIDE("Valide"),
    ANNULE("Annulé"),
    NON_VALIDE("Non valide");

    private final String text;

    BilletStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static BilletStatus fromString(String text) {
        if (text == null) {
            return VALIDE; // Default value
        }
        
        for (BilletStatus status : BilletStatus.values()) {
            if (status.text.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @Override
    public String toString() {
        return text;
    }
} 