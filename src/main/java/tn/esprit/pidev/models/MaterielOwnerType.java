package tn.esprit.pidev.models;

public enum MaterielOwnerType {
    CLUB("Club"),
    FEDERATION("Fédération"),
    PRIVE("Privé");

    private final String displayName;

    MaterielOwnerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static MaterielOwnerType fromDisplayName(String displayName) {
        for (MaterielOwnerType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No MaterielOwnerType with display name: " + displayName);
    }
} 