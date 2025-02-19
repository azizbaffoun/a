package tn.esprit.jappa.models;

public class User {
    private int id;
    private String prenom;
    private String nom;
    
    public User(int id, String prenom, String nom) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
    }
    
    public int getId() {
        return id;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public String getNom() {
        return nom;
    }
    
    @Override
    public String toString() {
        return String.format("%d - %s %s", id, prenom, nom);
    }
} 