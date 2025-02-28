package tn.esprit.jappa.models;

import javafx.beans.property.*;

public class TerrainSiege {
    private final IntegerProperty siegeID = new SimpleIntegerProperty();
    private final IntegerProperty terrainID = new SimpleIntegerProperty();
    private final StringProperty rangee = new SimpleStringProperty();
    private final StringProperty numero = new SimpleStringProperty();
    private final StringProperty statut = new SimpleStringProperty();
    private final DoubleProperty prix = new SimpleDoubleProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public TerrainSiege() {}

    public TerrainSiege(int siegeID, int terrainID, String rangee, String numero, String statut, double prix) {
        this.siegeID.set(siegeID);
        this.terrainID.set(terrainID);
        this.rangee.set(rangee);
        this.numero.set(numero);
        this.statut.set(statut);
        this.prix.set(prix);
    }

    public int getSiegeID() { return siegeID.get(); }
    public IntegerProperty siegeIDProperty() { return siegeID; }
    public void setSiegeID(int siegeID) { this.siegeID.set(siegeID); }

    public int getTerrainID() { return terrainID.get(); }
    public IntegerProperty terrainIDProperty() { return terrainID; }
    public void setTerrainID(int terrainID) { this.terrainID.set(terrainID); }

    public String getRangee() { return rangee.get(); }
    public StringProperty rangeeProperty() { return rangee; }
    public void setRangee(String rangee) { this.rangee.set(rangee); }

    public String getNumero() { return numero.get(); }
    public StringProperty numeroProperty() { return numero; }
    public void setNumero(String numero) { this.numero.set(numero); }

    public String getStatut() { return statut.get(); }
    public StringProperty statutProperty() { return statut; }
    public void setStatut(String statut) { this.statut.set(statut); }

    public double getPrix() { return prix.get(); }
    public DoubleProperty prixProperty() { return prix; }
    public void setPrix(double prix) { this.prix.set(prix); }

    public boolean isSelected() { return selected.get(); }
    public BooleanProperty selectedProperty() { return selected; }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    @Override
    public String toString() {
        return String.format("Siège %s-%s (%.2f€)", rangee.get(), numero.get(), prix.get());
    }
} 