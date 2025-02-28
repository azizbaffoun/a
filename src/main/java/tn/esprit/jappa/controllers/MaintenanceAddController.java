package tn.esprit.jappa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.jappa.models.Maintenance;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.models.MaintenanceStatus;
import tn.esprit.jappa.services.MaintenanceService;
import tn.esprit.jappa.services.MaterielService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MaintenanceAddController {
    private final MaintenanceService service;
    private final MaterielService materielService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ComboBox<Materiel> materielComboBox;
    @FXML private DatePicker dateMaintenancePicker;
    @FXML private TextArea descriptionField;
    @FXML private TextField coutField;
    @FXML private ComboBox<MaintenanceStatus> statutMaintenanceComboBox;

    public MaintenanceAddController() {
        this.service = new MaintenanceService();
        this.materielService = new MaterielService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutMaintenanceComboBox.setItems(FXCollections.observableArrayList(MaintenanceStatus.values()));
        refreshMaterielList();

        // Add tooltips
        materielComboBox.setTooltip(new Tooltip("Select the material to maintain"));
        dateMaintenancePicker.setTooltip(new Tooltip("Select the date of maintenance"));
        descriptionField.setTooltip(new Tooltip("Describe the maintenance work"));
        coutField.setTooltip(new Tooltip("Enter the maintenance cost"));
        statutMaintenanceComboBox.setTooltip(new Tooltip("Select the maintenance status"));
    }

    private void refreshMaterielList() {
        try {
            List<Materiel> materiels = materielService.getAll();
            materielComboBox.setItems(FXCollections.observableArrayList(materiels));
            materielComboBox.setCellFactory(param -> new ListCell<Materiel>() {
                @Override
                protected void updateItem(Materiel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getType() + " - " + item.getTypeSport() + " (ID: " + item.getId() + ")");
                    }
                }
            });
            materielComboBox.setButtonCell(materielComboBox.getCellFactory().call(null));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading material list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Maintenance maintenance = new Maintenance();
            maintenance.setMaterielID(materielComboBox.getValue().getId());
            maintenance.setDateMaintenance(dateMaintenancePicker.getValue());
            maintenance.setDescription(descriptionField.getText());
            maintenance.setCout(Double.parseDouble(coutField.getText()));
            maintenance.setStatutMaintenance(statutMaintenanceComboBox.getValue());

            service.add(maintenance);
            showAlert("Success", "Maintenance record added successfully!", Alert.AlertType.INFORMATION);
            clearFields();
        } catch (SQLException ex) {
            showAlert("Error", "Error adding maintenance record: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Invalid cost value. Please enter a valid number.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (materielComboBox.getValue() == null) {
            errorMessage.append("Material is required\n");
        }
        if (dateMaintenancePicker.getValue() == null) {
            errorMessage.append("Maintenance date is required\n");
        }
        if (descriptionField.getText().trim().isEmpty()) {
            errorMessage.append("Description is required\n");
        }
        if (coutField.getText().trim().isEmpty()) {
            errorMessage.append("Cost is required\n");
        } else {
            try {
                double cout = Double.parseDouble(coutField.getText().trim());
                if (cout <= 0) {
                    errorMessage.append("Cost must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Cost must be a valid number\n");
            }
        }
        if (statutMaintenanceComboBox.getValue() == null) {
            errorMessage.append("Status is required\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void clearFields() {
        materielComboBox.setValue(null);
        dateMaintenancePicker.setValue(null);
        descriptionField.clear();
        coutField.clear();
        statutMaintenanceComboBox.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 