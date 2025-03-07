package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.pidev.models.*;
import tn.esprit.pidev.services.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

public class EmpruntAddController {
    private final EmpruntService service;
    private final MaterielService materielService;
    private final UserService userService;
    private final MaintenanceService maintenanceService;
    private final AnalyticsService analyticsService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ComboBox<User> userIDComboBox;
    @FXML private ComboBox<Materiel> materielComboBox;
    @FXML private DatePicker dateEmpruntPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private ComboBox<EmpruntStatus> statutEmpruntComboBox;

    public EmpruntAddController() {
        this.service = new EmpruntService();
        this.materielService = new MaterielService();
        this.userService = new UserService();
        this.maintenanceService = new MaintenanceService();
        this.analyticsService = new AnalyticsService();
    }

    @FXML
    private void initialize() {
        refreshUserList();
        refreshMaterielList();
        statutEmpruntComboBox.setItems(FXCollections.observableArrayList(EmpruntStatus.values()));
        
        // Add listeners to date pickers
        dateEmpruntPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && materielComboBox.getValue() != null) {
                checkDateAvailability(newVal, materielComboBox.getValue());
            }
        });

        materielComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateEmpruntPicker.getValue() != null) {
                checkDateAvailability(dateEmpruntPicker.getValue(), newVal);
            }
        });

        // Set date picker constraints
        dateEmpruntPicker.setDayCellFactory(picker -> new DateCell() {
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

        dateRetourPicker.setDayCellFactory(picker -> new DateCell() {
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

    private void checkDateAvailability(LocalDate date, Materiel materiel) {
        try {
            boolean isDateTaken = service.isDateTaken(materiel.getId(), date);
            if (isDateTaken) {
                dateEmpruntPicker.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ef5350;");
                showAlert("Warning", "This material is already loaned on this date!", Alert.AlertType.WARNING);
            } else {
                dateEmpruntPicker.setStyle("");
            }
        } catch (SQLException ex) {
            showAlert("Error", "Error checking date availability: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshUserList() {
        try {
            userIDComboBox.setItems(FXCollections.observableArrayList(userService.getAll()));
            userIDComboBox.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNom() + " " + item.getPrenom());
                    }
                }
            });
            userIDComboBox.setButtonCell(userIDComboBox.getCellFactory().call(null));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading users: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshMaterielList() {
        try {
            materielComboBox.setItems(FXCollections.observableArrayList(materielService.getAll()));
            materielComboBox.setCellFactory(param -> new ListCell<Materiel>() {
                @Override
                protected void updateItem(Materiel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getType() + " - " + item.getTypeSport());
                    }
                }
            });
            materielComboBox.setButtonCell(materielComboBox.getCellFactory().call(null));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading materials: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            // Check if the date is already taken
            if (service.isDateTaken(materielComboBox.getValue().getId(), dateEmpruntPicker.getValue())) {
                showAlert("Error", "This material is already loaned on this date!", Alert.AlertType.ERROR);
                return;
            }

            // Create the loan
            Emprunt emprunt = new Emprunt();
            emprunt.setUserID(userIDComboBox.getValue().getId());
            emprunt.setMaterielID(materielComboBox.getValue().getId());
            emprunt.setDateEmprunt(dateEmpruntPicker.getValue());
            emprunt.setDateRetour(dateRetourPicker.getValue());
            emprunt.setStatutEmprunt(statutEmpruntComboBox.getValue());

            service.add(emprunt);
            analyticsService.trackEquipmentLoan(emprunt);

            // Check loan duration
            long loanDuration = ChronoUnit.DAYS.between(dateEmpruntPicker.getValue(), dateRetourPicker.getValue());

            // Create maintenance record if loan is more than 2 days
            if (loanDuration > 2) {
                Maintenance maintenance = new Maintenance();
                maintenance.setMaterielID(materielComboBox.getValue().getId());
                maintenance.setDateMaintenance(dateRetourPicker.getValue().plusDays(1));
                maintenance.setDescription("loaned many more");
                maintenance.setStatutMaintenance(MaintenanceStatus.SCHEDULED);
                
                maintenanceService.add(maintenance);
                analyticsService.trackMaintenance(maintenance);
                showAlert("Information", "Maintenance scheduled for the day after loan return.", Alert.AlertType.INFORMATION);
            }

            showAlert("Success", "Loan added successfully!", Alert.AlertType.INFORMATION);
            clearFields();
        } catch (SQLException ex) {
            showAlert("Error", "Error adding loan: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (userIDComboBox.getValue() == null) {
            errorMessage.append("Please select a user.\n");
        }
        if (materielComboBox.getValue() == null) {
            errorMessage.append("Please select a material.\n");
        }
        if (dateEmpruntPicker.getValue() == null) {
            errorMessage.append("Please select a loan date.\n");
        }
        if (dateRetourPicker.getValue() == null) {
            errorMessage.append("Please select a return date.\n");
        }
        if (statutEmpruntComboBox.getValue() == null) {
            errorMessage.append("Please select a status.\n");
        }

        if (dateEmpruntPicker.getValue() != null && dateRetourPicker.getValue() != null) {
            if (dateRetourPicker.getValue().isBefore(dateEmpruntPicker.getValue())) {
                errorMessage.append("Return date cannot be before loan date.\n");
            }
            if (dateEmpruntPicker.getValue().isBefore(LocalDate.now())) {
                errorMessage.append("Loan date cannot be in the past.\n");
            }
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void clearFields() {
        userIDComboBox.setValue(null);
        materielComboBox.setValue(null);
        dateEmpruntPicker.setValue(null);
        dateRetourPicker.setValue(null);
        statutEmpruntComboBox.setValue(null);
        dateEmpruntPicker.setStyle("");
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
        userIDComboBox.getScene().getWindow().hide();
    }
} 