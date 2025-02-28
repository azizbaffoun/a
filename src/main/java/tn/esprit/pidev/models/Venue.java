package tn.esprit.pidev.models;

public class Venue {
    private int courtID;
    private String type;
    private String localisation;
    private int capacite;
    private String statut;

    // Constructors
    public Venue() {}

    public Venue(int courtID, String type, String localisation, int capacite, String statut) {
        this.courtID = courtID;
        this.type = type;
        this.localisation = localisation;
        this.capacite = capacite;
        this.statut = statut;
    }

    // Getters and Setters
    public int getCourtID() { return courtID; }
    public void setCourtID(int courtID) { this.courtID = courtID; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Venue{" +
                "courtID=" + courtID +
                ", type='" + type + '\'' +
                ", localisation='" + localisation + '\'' +
                ", capacite=" + capacite +
                ", statut='" + statut + '\'' +
                '}';
    }
} 