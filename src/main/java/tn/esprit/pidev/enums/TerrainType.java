package tn.esprit.pidev.enums;

public enum TerrainType {
    TERRAIN("TERRAIN"),
    PADDEL("PADDEL");
    
    private final String value;
    
    TerrainType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TerrainType fromString(String text) {
        for (TerrainType type : TerrainType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 