package tn.esprit.jappa.models;

import java.time.LocalDate;

public class Event {
    private int id;
    private String nom;
    private String details;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type;
    private int participantsMax;

    public Event(int id, String nom, String details, LocalDate dateDebut, LocalDate dateFin, String type, int participantsMax) {
        this.id = id;
        this.nom = nom;
        this.details = details;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.participantsMax = participantsMax;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDetails() { return details; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public String getType() { return type; }
    public int getParticipantsMax() { return participantsMax; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDetails(String details) { this.details = details; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public void setType(String type) { this.type = type; }
    public void setParticipantsMax(int participantsMax) { this.participantsMax = participantsMax; }

    @Override
    public String toString() {
        return nom;
    }
} 