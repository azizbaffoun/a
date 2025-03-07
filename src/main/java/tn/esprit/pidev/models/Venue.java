package tn.esprit.pidev.models;

import tn.esprit.pidev.enums.TerrainType;
import tn.esprit.pidev.enums.TerrainStatus;

public class Venue {
    private int courtID;
    private TerrainType type;
    private String localisation;
    private int capacite;
    private TerrainStatus statut;

    // Constructors
    public Venue() {}

    public Venue(int courtID, TerrainType type, String localisation, int capacite, TerrainStatus statut) {
        this.courtID = courtID;
        this.type = type;
        this.localisation = localisation;
        this.capacite = capacite;
        this.statut = statut;
    }

    // Getters and Setters
    public int getCourtID() { return courtID; }
    public void setCourtID(int courtID) { this.courtID = courtID; }

    public TerrainType getType() { return type; }
    public void setType(TerrainType type) { this.type = type; }
    public void setType(String type) { this.type = TerrainType.valueOf(type); }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public TerrainStatus getStatut() { return statut; }
    public void setStatut(TerrainStatus statut) { this.statut = statut; }
    public void setStatut(String statut) { this.statut = TerrainStatus.valueOf(statut); }

    @Override
    public String toString() {
        return "Venue{" +
                "courtID=" + courtID +
                ", type=" + type +
                ", localisation='" + localisation + '\'' +
                ", capacite=" + capacite +
                ", statut=" + statut +
                '}';
    }
} 