package tn.esprit.jappa.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Billet {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty eventId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> dateAchat = new SimpleObjectProperty<>();
    private final DoubleProperty prix = new SimpleDoubleProperty();
    private final StringProperty typeBillet = new SimpleStringProperty();
    private final StringProperty statut = new SimpleStringProperty();
    private final IntegerProperty quantite = new SimpleIntegerProperty();
    private final ObjectProperty<Event> event = new SimpleObjectProperty<>();

    public Billet() {}

    public Billet(int eventId, LocalDate dateAchat, double prix, String typeBillet, String statut, int quantite) {
        this.eventId.set(eventId);
        this.dateAchat.set(dateAchat);
        this.prix.set(prix);
        this.typeBillet.set(typeBillet);
        this.statut.set(statut);
        this.quantite.set(quantite);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getEventID() { return eventId.get(); }
    public IntegerProperty eventIDProperty() { return eventId; }
    public void setEventID(int eventId) { this.eventId.set(eventId); }

    public LocalDate getDateAchat() { return dateAchat.get(); }
    public ObjectProperty<LocalDate> dateAchatProperty() { return dateAchat; }
    public void setDateAchat(LocalDate date) { this.dateAchat.set(date); }

    public double getPrix() { return prix.get(); }
    public DoubleProperty prixProperty() { return prix; }
    public void setPrix(double prix) { this.prix.set(prix); }

    public String getTypeBillet() { return typeBillet.get(); }
    public StringProperty typeBilletProperty() { return typeBillet; }
    public void setTypeBillet(String type) { this.typeBillet.set(type); }

    public String getStatut() { return statut.get(); }
    public StringProperty statutProperty() { return statut; }
    public void setStatut(String statut) { this.statut.set(statut); }

    public int getQuantite() { return quantite.get(); }
    public IntegerProperty quantiteProperty() { return quantite; }
    public void setQuantite(int quantite) { this.quantite.set(quantite); }

    public Event getEvent() { return event.get(); }
    public ObjectProperty<Event> eventProperty() { return event; }
    public void setEvent(Event event) { this.event.set(event); }

    @Override
    public String toString() {
        Event currentEvent = event.get();
        return String.format("%s - %s (%.2f€) - %d available", 
            typeBillet.get(), 
            currentEvent != null ? currentEvent.getNom() : "Unknown Event", 
            prix.get(), 
            quantite.get());
    }
} 