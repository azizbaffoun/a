package tn.esprit.jappa.models;

public class Materiel {
    private int id;
    private String type;
    private String typeSport;
    private double prix;
    private String dateReservation;
    private String statut;
    private String ownerType;

    // Constructors
    public Materiel() {}

    public Materiel(int id, String type, String typeSport, double prix, String dateReservation, String statut, String ownerType) {
        this.id = id;
        this.type = type;
        this.typeSport = typeSport;
        this.prix = prix;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.ownerType = ownerType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeSport() {
        return typeSport;
    }

    public void setTypeSport(String typeSport) {
        this.typeSport = typeSport;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    @Override
    public String toString() {
        return String.format("%-8d %-20s %-20s %-15.2f %-15s %-15s",
                id,
                type,
                typeSport,
                prix,
                ownerType,
                statut);
    }
} 