package tn.esprit.jappa.models;

public class ReservationBillet {
    private int reservationID;
    private int billetID;
    private int nombreBillet;
    private Billet billet; // Reference to the associated Billet
    private User user; // Add this field

    public ReservationBillet() {}

    public ReservationBillet(int billetID, int nombreBillet, Billet billet) {
        this.billetID = billetID;
        this.nombreBillet = nombreBillet;
        this.billet = billet; // Set billet
    }

    // Getters
    public int getReservationID() { return reservationID; }
    public int getBilletID() { return billetID; }
    public int getNombreBillet() { return nombreBillet; }
    public Billet getBillet() { return billet; }
    public User getUser() { return user; }

    // Setters
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }
    public void setBilletID(int billetID) { this.billetID = billetID; }
    public void setNombreBillet(int nombreBillet) { this.nombreBillet = nombreBillet; }
    public void setBillet(Billet billet) { this.billet = billet; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        String userName = (user != null) ? user.getPrenom() + " " + user.getNom() : "Unknown User";
        String billetInfo = (billet != null) ? billet.toString() : "Billet #" + billetID;
        return "Reservation #" + reservationID + " - " + userName + " - " + billetInfo + " - Quantity: " + nombreBillet;
    }
} 