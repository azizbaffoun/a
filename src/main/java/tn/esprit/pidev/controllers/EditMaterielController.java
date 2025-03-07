package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.models.MaterielStatus;
import tn.esprit.pidev.services.MaterielService;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditMaterielController {
    @FXML private TextField typeField;
    @FXML private TextField typeSportField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<MaterielStatus> statutMaterielComboBox;

    private MaterielService service;
    private Materiel currentMateriel;
    private int materielId; // ID of the Materiel to edit

    public EditMaterielController() {
        this.service = new MaterielService();
    }

    @FXML
    private void initialize() {
        try {
            loadMaterielData();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load Materiel data: " + e.getMessage());
        }
    }

    private void loadMaterielData() throws SQLException {
        // Logic to load the current Materiel data into the fields
        currentMateriel = service.getById(materielId);
        typeField.setText(currentMateriel.getType());
        typeSportField.setText(currentMateriel.getTypeSport());
        prixField.setText(String.valueOf(currentMateriel.getPrix()));
        dateReservationPicker.setValue(LocalDate.parse(currentMateriel.getDateReservation()));
        statutMaterielComboBox.setValue(MaterielStatus.valueOf(currentMateriel.getStatut()));
    }

    @FXML
    public void handleSave() {
        currentMateriel.setType(typeField.getText());
        currentMateriel.setTypeSport(typeSportField.getText());
        currentMateriel.setPrix(Double.parseDouble(prixField.getText()));
        currentMateriel.setDateReservation(dateReservationPicker.getValue().toString());
        currentMateriel.setStatut(statutMaterielComboBox.getValue().toString());

        try {
            service.update(currentMateriel);
            showAlert("Success", "Materiel updated successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update Materiel: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        // Logic to go back to the previous screen
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 