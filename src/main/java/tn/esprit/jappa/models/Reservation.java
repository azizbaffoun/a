package tn.esprit.jappa.models;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int utilisateurID;
    private LocalDate dateReservation;
    private String statut;

    public Reservation() {}

    public Reservation(int utilisateurID, LocalDate dateReservation, String statut) {
        this.utilisateurID = utilisateurID;
        this.dateReservation = dateReservation;
        this.statut = statut;
    }

    // Getters
    public int getId() { return id; }
    public int getUtilisateurID() { return utilisateurID; }
    public LocalDate getDateReservation() { return dateReservation; }
    public String getStatut() { return statut; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUtilisateurID(int utilisateurID) { this.utilisateurID = utilisateurID; }
    public void setDateReservation(LocalDate dateReservation) { this.dateReservation = dateReservation; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Reservation #" + id + " - User: " + utilisateurID + " - Date: " + dateReservation;
    }
} 