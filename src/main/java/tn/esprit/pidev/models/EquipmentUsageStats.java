package tn.esprit.pidev.models;

import javafx.beans.property.*;

public class EquipmentUsageStats {
    private final StringProperty equipmentId;
    private final StringProperty equipmentType;
    private final IntegerProperty loanCount;
    private final StringProperty lastLoanDate;

    public EquipmentUsageStats(String equipmentId, String equipmentType, int loanCount, String lastLoanDate) {
        this.equipmentId = new SimpleStringProperty(equipmentId);
        this.equipmentType = new SimpleStringProperty(equipmentType);
        this.loanCount = new SimpleIntegerProperty(loanCount);
        this.lastLoanDate = new SimpleStringProperty(lastLoanDate);
    }

    public StringProperty equipmentIdProperty() {
        return equipmentId;
    }

    public StringProperty equipmentTypeProperty() {
        return equipmentType;
    }

    public IntegerProperty loanCountProperty() {
        return loanCount;
    }

    public StringProperty lastLoanDateProperty() {
        return lastLoanDate;
    }

    public String getEquipmentId() {
        return equipmentId.get();
    }

    public String getEquipmentType() {
        return equipmentType.get();
    }

    public int getLoanCount() {
        return loanCount.get();
    }

    public String getLastLoanDate() {
        return lastLoanDate.get();
    }

    public void setEquipmentId(String value) {
        equipmentId.set(value);
    }

    public void setEquipmentType(String value) {
        equipmentType.set(value);
    }

    public void setLoanCount(int value) {
        loanCount.set(value);
    }

    public void setLastLoanDate(String value) {
        lastLoanDate.set(value);
    }
} 