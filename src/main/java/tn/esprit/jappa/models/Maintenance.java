package tn.esprit.jappa.models;

import java.time.LocalDate;

public class Maintenance {
    private int maintenanceID;
    private int materielID;
    private LocalDate dateMaintenance;
    private String description;
    private double cout;
    private MaintenanceStatus statutMaintenance;

    // Constructors
    public Maintenance() {}

    public Maintenance(int maintenanceID, int materielID, LocalDate dateMaintenance, String description, double cout, MaintenanceStatus statutMaintenance) {
        this.maintenanceID = maintenanceID;
        this.materielID = materielID;
        this.dateMaintenance = dateMaintenance;
        this.description = description;
        this.cout = cout;
        this.statutMaintenance = statutMaintenance;
    }

    // Getters and Setters
    public int getMaintenanceID() {
        return maintenanceID;
    }

    public void setMaintenanceID(int maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public int getMaterielID() {
        return materielID;
    }

    public void setMaterielID(int materielID) {
        this.materielID = materielID;
    }

    public LocalDate getDateMaintenance() {
        return dateMaintenance;
    }

    public void setDateMaintenance(LocalDate dateMaintenance) {
        this.dateMaintenance = dateMaintenance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public MaintenanceStatus getStatutMaintenance() {
        return statutMaintenance;
    }

    public void setStatutMaintenance(MaintenanceStatus statutMaintenance) {
        this.statutMaintenance = statutMaintenance;
    }

    @Override
    public String toString() {
        return String.format("Maintenance #%d for Material %d on %s (%s)", 
            maintenanceID, materielID, dateMaintenance, statutMaintenance);
    }
} 