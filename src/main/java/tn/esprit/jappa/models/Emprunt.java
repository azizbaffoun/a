package tn.esprit.jappa.models;

public class Emprunt {
    private int empruntID;
    private int userID;
    private int materielID;
    private String dateEmprunt;
    private String dateRetour;
    private String statutEmprunt;

    // Constructors
    public Emprunt() {}

    public Emprunt(int empruntID, int userID, int materielID, String dateEmprunt, String dateRetour, String statutEmprunt) {
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

    public String getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(String dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public String getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(String dateRetour) {
        this.dateRetour = dateRetour;
    }

    public String getStatutEmprunt() {
        return statutEmprunt;
    }

    public void setStatutEmprunt(String statutEmprunt) {
        this.statutEmprunt = statutEmprunt;
    }

    @Override
    public String toString() {
        return String.format("%-8d %-20s %-20s %-15s %-15d %-15d",
                empruntID,
                dateEmprunt,
                dateRetour,
                statutEmprunt,
                userID,
                materielID);
    }
} 