package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.pidev.models.Maintenance;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.models.MaintenanceStatus;
import tn.esprit.pidev.services.MaintenanceService;
import tn.esprit.pidev.services.MaterielService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MaintenanceEditController {
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

    public MaintenanceEditController() {
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
        materielComboBox.setTooltip(new Tooltip("Select the material for maintenance"));
        dateMaintenancePicker.setTooltip(new Tooltip("Select the maintenance date"));
        descriptionField.setTooltip(new Tooltip("Enter maintenance description"));
        coutField.setTooltip(new Tooltip("Enter maintenance cost"));
        statutMaintenanceComboBox.setTooltip(new Tooltip("Select the maintenance status"));

        // Set date picker constraints
        dateMaintenancePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
                if (date.isBefore(today)) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Light red for past dates
                }
            }
        });
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
                        setText(item.toString());
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
        Maintenance selected = maintenanceListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance record to update", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInput()) {
            return;
        }

        updateMaintenance(selected);
    }

    private void updateMaintenance(Maintenance selected) {
        try {
            selected.setMaterielID(materielComboBox.getValue().getId());
            selected.setDateMaintenance(dateMaintenancePicker.getValue());
            selected.setDescription(descriptionField.getText());
            // selected.setCout(Double.parseDouble(coutField.getText()));
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
            // coutField.setText(String.valueOf(maintenance.getCout()));
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
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            errorMessage.append("Description is required\n");
        }
        if (coutField.getText() == null || coutField.getText().trim().isEmpty()) {
            errorMessage.append("Cost is required\n");
        } else {
            try {
                double cout = Double.parseDouble(coutField.getText().trim());
                if (cout < 0) {
                    errorMessage.append("Cost must be a positive number\n");
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
        descriptionField.setText("");
        coutField.setText("");
        statutMaintenanceComboBox.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        // Get the current stage and close it to return to previous screen
        maintenanceListView.getScene().getWindow().hide();
    }
} 