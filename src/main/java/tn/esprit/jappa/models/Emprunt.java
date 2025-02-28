package tn.esprit.jappa.models;

import java.time.LocalDate;

public class Emprunt {
    private int empruntID;
    private int userID;
    private int materielID;
    private LocalDate dateEmprunt;
    private LocalDate dateRetour;
    private EmpruntStatus statutEmprunt;

    // Constructors
    public Emprunt() {}

    public Emprunt(int empruntID, int userID, int materielID, LocalDate dateEmprunt, LocalDate dateRetour, EmpruntStatus statutEmprunt) {
        this.empruntID = empruntID;
        this.userID = userID;
        this.materielID = materielID;
        this.dateEmprunt = dateEmprunt;
        this.dateRetour = dateRetour;
        this.statutEmprunt = statutEmprunt;
    }

    // Getters and Setters
    public int getEmpruntID() {
        return empruntID;
    }

    public void setEmpruntID(int empruntID) {
        this.empruntID = empruntID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getMaterielID() {
        return materielID;
    }

    public void setMaterielID(int materielID) {
        this.materielID = materielID;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public EmpruntStatus getStatutEmprunt() {
        return statutEmprunt;
    }

    public void setStatutEmprunt(EmpruntStatus statutEmprunt) {
        this.statutEmprunt = statutEmprunt;
    }

    @Override
    public String toString() {
        return String.format("Loan #%d: Material %d borrowed by User %d from %s to %s (%s)",
            empruntID, materielID, userID, dateEmprunt, dateRetour, statutEmprunt);
    }
} 