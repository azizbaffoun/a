package tn.esprit.jappa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import tn.esprit.jappa.models.Maintenance;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.models.MaintenanceStatus;
import tn.esprit.jappa.services.MaintenanceService;
import tn.esprit.jappa.services.MaterielService;
import tn.esprit.jappa.services.EmpruntService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MaintenanceController {
    private final MaintenanceService service;
    private final MaterielService materielService;
    private final EmpruntService empruntService;
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
        this.empruntService = new EmpruntService();
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
                setDisable(empty || date.compareTo(today) < 0);
                if (date.compareTo(today) < 0) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Light red for past dates
                }
            }
        });

        // Create buttons with consistent width
        HBox buttonBox = new HBox(10);

        Button[] buttons = {
            new Button("Add New Maintenance"),
            new Button("Edit Maintenance"),
            new Button("Delete Maintenance"),
            new Button("Clear")
        };

        // Add event handlers
        buttons[0].setOnAction(e -> handleAdd());
        buttons[1].setOnAction(e -> handleUpdate());
        buttons[2].setOnAction(e -> handleDelete());
        buttons[3].setOnAction(e -> handleClear());

        buttonBox.getChildren().addAll(buttons);
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

    private void setupListView() {
        maintenanceListView.setCellFactory(param -> new ListCell<Maintenance>() {
            @Override
            protected void updateItem(Maintenance item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    try {
                        Materiel materiel = materielService.getById(item.getMaterielID());
                        setText(String.format("Maintenance #%d - %s - %s - %s - %.2f",
                            item.getMaintenanceID(),
                            materiel.getType(),
                            item.getDateMaintenance().format(DATE_FORMATTER),
                            item.getStatutMaintenance().getDisplayName(),
                            item.getCout()));
                    } catch (SQLException ex) {
                        setText("Error loading maintenance details");
                    }
                }
            }
        });
        refreshList();
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
            refreshList();
            showAlert("Success", "Maintenance record added successfully!", Alert.AlertType.INFORMATION);
            clearFields();
        } catch (SQLException ex) {
            showAlert("Error", "Error adding maintenance record: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Invalid cost value. Please enter a valid number.", Alert.AlertType.ERROR);
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

        // Check if maintenance can be deleted
        try {
            // Check if maintenance is in progress
            if (selected.getStatutMaintenance() == MaintenanceStatus.IN_PROGRESS) {
                showAlert("Error", "Cannot delete maintenance that is in progress", Alert.AlertType.ERROR);
                return;
            }

            // Check if the material is currently on loan
            if (empruntService.isDateTaken(selected.getMaterielID(), LocalDate.now())) {
                showAlert("Error", "Cannot delete maintenance record while the material is on loan", Alert.AlertType.ERROR);
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete this maintenance record?");

            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                deleteMaintenance(selected);
            }
        } catch (SQLException ex) {
            showAlert("Error", "Error checking maintenance status: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteMaintenance(Maintenance selected) {
        try {
            // Set status to CANCELLED instead of deleting if there are dependencies
            if (hasDependencies(selected)) {
                selected.setStatutMaintenance(MaintenanceStatus.CANCELLED);
                service.update(selected);
                showAlert("Success", "Maintenance record has been cancelled due to existing dependencies.", Alert.AlertType.INFORMATION);
            } else {
                service.delete(selected.getMaintenanceID());
                showAlert("Success", "Maintenance record deleted successfully!", Alert.AlertType.INFORMATION);
            }
            refreshList();
            clearFields();
        } catch (SQLException ex) {
            showAlert("Error", "Error deleting maintenance record: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean hasDependencies(Maintenance maintenance) throws SQLException {
        // Check for any dependencies that would prevent deletion
        // For example, check if this maintenance is referenced by other records
        // This is a placeholder - implement actual dependency checks based on your data model
        return false;
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

    private void clearFields() {
        materielComboBox.setValue(null);
        dateMaintenancePicker.setValue(null);
        descriptionField.setText("");
        coutField.setText("");
        statutMaintenanceComboBox.setValue(null);
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (materielComboBox.getValue() == null) {
            errorMessage.append("Material is required\n");
        }
        
        LocalDate selectedDate = dateMaintenancePicker.getValue();
        LocalDate today = LocalDate.now();
        if (selectedDate == null) {
            errorMessage.append("Maintenance date is required\n");
        } else if (selectedDate.compareTo(today) < 0) {
            errorMessage.append("Maintenance date cannot be in the past\n");
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 