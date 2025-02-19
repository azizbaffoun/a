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

public class MaintenanceController {
    private final MaintenanceService service;
    private final MaterielService materielService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ComboBox<Materiel> materielComboBox;
    @FXML private DatePicker dateMaintenancePicker;
    @FXML private TextArea descriptionField;
    @FXML private TextField coutField;
    @FXML private ComboBox<MaintenanceStatus> statutMaintenanceComboBox;
    @FXML private ListView<Maintenance> maintenanceListView;
    @FXML private Label listHeader;

    public MaintenanceController() {
        this.service = new MaintenanceService();
        this.materielService = new MaterielService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutMaintenanceComboBox.setItems(FXCollections.observableArrayList(MaintenanceStatus.values()));
        refreshMaterielList();

        // Setup ListView header
        String headerFormat = "%-8s %-20s %-20s %-15s %-15s %-15s";
        listHeader.setText(String.format(headerFormat,
                "ID", "Date", "Description", "Cost", "Status", "Material"));

        // Setup ListView
        setupListView();
        refreshList();

        // Add tooltips
        materielComboBox.setTooltip(new Tooltip("Select the materiel to maintain"));
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
            showAlert("Error", "Error loading materiel list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupListView() {
        maintenanceListView.setCellFactory(param -> new ListCell<Maintenance>() {
            @Override
            protected void updateItem(Maintenance item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: " + (getIndex() % 2 == 0 ? "#f5f5f5" : "#ffffff") + ";");
                }
            }
        });

        maintenanceListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Maintenance maintenance = new Maintenance();
            maintenance.setMaterielID(materielComboBox.getValue().getId());
            maintenance.setDateMaintenance(dateMaintenancePicker.getValue().format(DATE_FORMATTER));
            maintenance.setDescription(descriptionField.getText());
            maintenance.setCout(Double.parseDouble(coutField.getText()));
            maintenance.setStatutMaintenance(statutMaintenanceComboBox.getValue().getDisplayName());

            service.add(maintenance);
            refreshList();
            clearFields();
            showAlert("Success", "Maintenance added successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error adding maintenance: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid cost!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        Maintenance selected = maintenanceListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance to update!", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            selected.setMaterielID(materielComboBox.getValue().getId());
            selected.setDateMaintenance(dateMaintenancePicker.getValue().format(DATE_FORMATTER));
            selected.setDescription(descriptionField.getText());
            selected.setCout(Double.parseDouble(coutField.getText()));
            selected.setStatutMaintenance(statutMaintenanceComboBox.getValue().getDisplayName());

            service.update(selected);
            refreshList();
            showAlert("Success", "Maintenance updated successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error updating maintenance: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid cost!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Maintenance selected = maintenanceListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance to delete!", Alert.AlertType.ERROR);
            return;
        }

        try {
            service.delete(selected.getMaintenanceID());
            refreshList();
            clearFields();
            showAlert("Success", "Maintenance deleted successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error deleting maintenance: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void refreshList() {
        try {
            maintenanceListView.setItems(FXCollections.observableArrayList(service.getAll()));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading maintenance list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void fillFields(Maintenance maintenance) {
        try {
            Materiel materiel = materielService.getById(maintenance.getMaterielID());
            materielComboBox.setValue(materiel);
        } catch (SQLException ex) {
            showAlert("Error", "Error loading materiel: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        
        dateMaintenancePicker.setValue(LocalDate.parse(maintenance.getDateMaintenance(), DATE_FORMATTER));
        descriptionField.setText(maintenance.getDescription());
        coutField.setText(String.valueOf(maintenance.getCout()));
        statutMaintenanceComboBox.setValue(MaintenanceStatus.valueOf(maintenance.getStatutMaintenance().toUpperCase().replace(" ", "_")));
    }

    private void clearFields() {
        materielComboBox.setValue(null);
        dateMaintenancePicker.setValue(null);
        descriptionField.clear();
        coutField.clear();
        statutMaintenanceComboBox.setValue(null);
        maintenanceListView.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (materielComboBox.getValue() == null) {
            errorMessage.append("Materiel selection is required.\n");
        }
        if (dateMaintenancePicker.getValue() == null) {
            errorMessage.append("Maintenance date is required.\n");
        }
        if (descriptionField.getText().trim().isEmpty()) {
            errorMessage.append("Description is required.\n");
        }
        if (coutField.getText().trim().isEmpty()) {
            errorMessage.append("Cost is required.\n");
        } else {
            try {
                double cost = Double.parseDouble(coutField.getText());
                if (cost < 0) {
                    errorMessage.append("Cost must be positive.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Cost must be a valid number.\n");
            }
        }
        if (statutMaintenanceComboBox.getValue() == null) {
            errorMessage.append("Status is required.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 