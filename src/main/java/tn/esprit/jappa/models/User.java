package tn.esprit.jappa.models;

public class User {
    private int id;
    private String prenom;
    private String nom;

    public User() {}

    public User(int id, String prenom, String nom) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
    }

    // Getters
    public int getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
} 