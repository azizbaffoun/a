package tn.esprit.pidev.models;

import java.util.Date;

public class Event {
    private int ID;
    private String nom;
    private String details;
    private Date dateDebut;
    private Date dateFin;
    private String type;
    private String recompense;
    private String statut;
    private int participantsMax;

    // Constructors
    public Event() {}

    public Event(int ID, String nom, String details, Date dateDebut, Date dateFin, 
                String type, String recompense, String statut, int participantsMax) {
        this.ID = ID;
        this.nom = nom;
        this.details = details;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.recompense = recompense;
        this.statut = statut;
        this.participantsMax = participantsMax;
    }

    // Getters and Setters
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRecompense() { return recompense; }
    public void setRecompense(String recompense) { this.recompense = recompense; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getParticipantsMax() { return participantsMax; }
    public void setParticipantsMax(int participantsMax) { this.participantsMax = participantsMax; }

    @Override
    public String toString() {
        return "Event{" +
                "ID=" + ID +
                ", nom='" + nom + '\'' +
                ", details='" + details + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", type='" + type + '\'' +
                ", recompense='" + recompense + '\'' +
                ", statut='" + statut + '\'' +
                ", participantsMax=" + participantsMax +
                '}';
    }
} 