package tn.esprit.jappa.models;

public class Maintenance {
    private int maintenanceID;
    private int materielID;
    private String dateMaintenance;
    private String description;
    private String statutMaintenance;
    private double cout;

    // Constructors
    public Maintenance() {}

    public Maintenance(int maintenanceID, int materielID, String dateMaintenance, String description, String statutMaintenance) {
        this.maintenanceID = maintenanceID;
        this.materielID = materielID;
        this.dateMaintenance = dateMaintenance;
        this.description = description;
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

    public String getDateMaintenance() {
        return dateMaintenance;
    }

    public void setDateMaintenance(String dateMaintenance) {
        this.dateMaintenance = dateMaintenance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatutMaintenance() {
        return statutMaintenance;
    }

    public void setStatutMaintenance(String statutMaintenance) {
        this.statutMaintenance = statutMaintenance;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    @Override
    public String toString() {
        return String.format("%-8d %-20s %-20s %-15.2f %-15s %-15s",
                maintenanceID,
                dateMaintenance,
                description.length() > 20 ? description.substring(0, 17) + "..." : description,
                cout,
                statutMaintenance,
                materielID);
    }
} 