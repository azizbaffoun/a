package tn.esprit.pidev.models;

import tn.esprit.pidev.enums.BilletStatus;
import java.time.LocalDateTime;

public class Billet {
    private Long id;
    private LocalDateTime dateAchat;
    private double prix;
    private int quantite;
    private BilletStatus status;
    private Long evenementId;

    public Billet() {
        this.status = BilletStatus.VALIDE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDateTime dateAchat) {
        this.dateAchat = dateAchat;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BilletStatus getStatus() {
        return status;
    }

    public void setStatus(BilletStatus status) {
        this.status = status;
    }

    public Long getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }
} 