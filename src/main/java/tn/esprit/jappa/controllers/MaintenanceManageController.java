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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MaintenanceManageController {
    private final MaintenanceService service;
    private final MaterielService materielService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ListView<Maintenance> maintenanceListView;
    @FXML private ComboBox<Materiel> materielComboBox;
    @FXML private DatePicker dateMaintenancePicker;
    @FXML private TextArea descriptionField;
    @FXML private TextField coutField;
    @FXML private ComboBox<MaintenanceStatus> statutMaintenanceComboBox;
    @FXML private Label listHeader;

    public MaintenanceManageController() {
        this.service = new MaintenanceService();
        this.materielService = new MaterielService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutMaintenanceComboBox.setItems(FXCollections.observableArrayList(MaintenanceStatus.values()));
        refreshMaterielList();
        setupListView();

        // Add tooltips
        materielComboBox.setTooltip(new Tooltip("Select the material to maintain"));
        dateMaintenancePicker.setTooltip(new Tooltip("Select the date of maintenance"));
        descriptionField.setTooltip(new Tooltip("Describe the maintenance work"));
        coutField.setTooltip(new Tooltip("Enter the maintenance cost"));
        statutMaintenanceComboBox.setTooltip(new Tooltip("Select the maintenance status"));
    }

    private void setupListView() {
        try {
            List<Maintenance> maintenances = service.getAll();
            maintenanceListView.setItems(FXCollections.observableArrayList(maintenances));
            maintenanceListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    fillFields(newVal);
                }
            });
        } catch (SQLException ex) {
            showAlert("Error", "Error loading maintenance list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
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
    private void handleUpdate() {
        if (!validateInput()) {
            return;
        }

        Maintenance selected = maintenanceListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance record to update", Alert.AlertType.ERROR);
            return;
        }

        updateMaintenance(selected);
    }

    private void updateMaintenance(Maintenance selected) {
        try {
            selected.setMaterielID(materielComboBox.getValue().getId());
            selected.setDateMaintenance(dateMaintenancePicker.getValue());
            selected.setDescription(descriptionField.getText());
            selected.setCout(Double.parseDouble(coutField.getText()));
            selected.setStatutMaintenance(statutMaintenanceComboBox.getValue());

            service.update(selected);
            refreshList();
            showAlert("Success", "Maintenance record updated successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error updating maintenance record: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Invalid cost value. Please enter a valid number.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Maintenance selected = maintenanceListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance record to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this maintenance record?");

        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            deleteMaintenance(selected);
        }
    }

    private void deleteMaintenance(Maintenance selected) {
        try {
            service.delete(selected.getMaintenanceID());
            refreshList();
            clearFields();
            showAlert("Success", "Maintenance record deleted successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error deleting maintenance record: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void refreshList() {
        try {
            List<Maintenance> maintenances = service.getAll();
            maintenanceListView.setItems(FXCollections.observableArrayList(maintenances));
        } catch (SQLException ex) {
            showAlert("Error", "Error refreshing maintenance list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void fillFields(Maintenance maintenance) {
        try {
            Materiel materiel = materielService.getById(maintenance.getMaterielID());
            materielComboBox.setValue(materiel);
            dateMaintenancePicker.setValue(maintenance.getDateMaintenance());
            descriptionField.setText(maintenance.getDescription());
            coutField.setText(String.valueOf(maintenance.getCout()));
            statutMaintenanceComboBox.setValue(maintenance.getStatutMaintenance());
        } catch (SQLException ex) {
            showAlert("Error", "Error loading maintenance details: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
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