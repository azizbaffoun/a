package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.models.MaterielStatus;
import tn.esprit.pidev.services.MaterielService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MaterielAddController {
    private final MaterielService service;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private TextField typeField;
    @FXML private TextField typeSportField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<MaterielStatus> statutComboBox;
    @FXML private ComboBox<String> ownerTypeComboBox;

    public MaterielAddController() {
        this.service = new MaterielService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutComboBox.setItems(FXCollections.observableArrayList(MaterielStatus.values()));
        ownerTypeComboBox.setItems(FXCollections.observableArrayList("Club", "Fédération", "Privé"));

        // Add tooltips
        typeField.setTooltip(new Tooltip("Enter the type of material"));
        typeSportField.setTooltip(new Tooltip("Enter the sport type"));
        prixField.setTooltip(new Tooltip("Enter the price"));
        dateReservationPicker.setTooltip(new Tooltip("Select the reservation date"));
        statutComboBox.setTooltip(new Tooltip("Select the material status"));
        ownerTypeComboBox.setTooltip(new Tooltip("Select the owner type"));

        // Set date picker constraints
        dateReservationPicker.setDayCellFactory(picker -> new DateCell() {
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

    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Materiel materiel = new Materiel();
            materiel.setType(typeField.getText());
            materiel.setTypeSport(typeSportField.getText());
            materiel.setPrix(Double.parseDouble(prixField.getText()));
            materiel.setDateReservation(dateReservationPicker.getValue().format(DATE_FORMATTER));
            materiel.setStatut(statutComboBox.getValue().getDisplayName());
            materiel.setOwnerType(ownerTypeComboBox.getValue());

            service.add(materiel);
            showAlert("Success", "Material added successfully!", Alert.AlertType.INFORMATION);
            clearFields();
        } catch (SQLException ex) {
            showAlert("Error", "Error adding material: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid price!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (typeField.getText().trim().isEmpty()) {
            errorMessage.append("Type is required\n");
        }
        if (typeSportField.getText().trim().isEmpty()) {
            errorMessage.append("Sport type is required\n");
        }
        if (prixField.getText().trim().isEmpty()) {
            errorMessage.append("Price is required\n");
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText().trim());
                if (prix <= 0) {
                    errorMessage.append("Price must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a valid number\n");
            }
        }
        if (dateReservationPicker.getValue() == null) {
            errorMessage.append("Reservation date is required\n");
        }
        if (statutComboBox.getValue() == null) {
            errorMessage.append("Status is required\n");
        }
        if (ownerTypeComboBox.getValue() == null) {
            errorMessage.append("Owner type is required\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void clearFields() {
        typeField.clear();
        typeSportField.clear();
        prixField.clear();
        dateReservationPicker.setValue(null);
        statutComboBox.setValue(null);
        ownerTypeComboBox.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 