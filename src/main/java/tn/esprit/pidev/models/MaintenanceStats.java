package tn.esprit.pidev.models;

import javafx.beans.property.*;

public class MaintenanceStats {
    private final StringProperty equipmentId;
    private final IntegerProperty maintenanceCount;
    private final StringProperty lastMaintenanceDate;
    private final StringProperty status;

    public MaintenanceStats(String equipmentId, int maintenanceCount, String lastMaintenanceDate, String status) {
        this.equipmentId = new SimpleStringProperty(equipmentId);
        this.maintenanceCount = new SimpleIntegerProperty(maintenanceCount);
        this.lastMaintenanceDate = new SimpleStringProperty(lastMaintenanceDate);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty equipmentIdProperty() {
        return equipmentId;
    }

    public IntegerProperty maintenanceCountProperty() {
        return maintenanceCount;
    }

    public StringProperty lastMaintenanceDateProperty() {
        return lastMaintenanceDate;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getEquipmentId() {
        return equipmentId.get();
    }

    public int getMaintenanceCount() {
        return maintenanceCount.get();
    }

    public String getLastMaintenanceDate() {
        return lastMaintenanceDate.get();
    }

    public String getStatus() {
        return status.get();
    }

    public void setEquipmentId(String value) {
        equipmentId.set(value);
    }

    public void setMaintenanceCount(int value) {
        maintenanceCount.set(value);
    }

    public void setLastMaintenanceDate(String value) {
        lastMaintenanceDate.set(value);
    }

    public void setStatus(String value) {
        status.set(value);
    }
} 