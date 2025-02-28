package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.models.MaterielStatus;
import tn.esprit.jappa.services.MaterielService;
import java.sql.SQLException;

public class AddMaterielController {
    @FXML private TextField typeField;
    @FXML private TextField typeSportField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<MaterielStatus> statutMaterielComboBox;

    private MaterielService service;

    public AddMaterielController() {
        this.service = new MaterielService();
    }

    @FXML
    public void handleAdd() {
        Materiel newMateriel = new Materiel();
        newMateriel.setType(typeField.getText());
        newMateriel.setTypeSport(typeSportField.getText());
        newMateriel.setPrix(Double.parseDouble(prixField.getText()));
        newMateriel.setDateReservation(dateReservationPicker.getValue().toString());
        newMateriel.setStatut(statutMaterielComboBox.getValue().toString());

        try {
            service.add(newMateriel);
            showAlert("Success", "Materiel added successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add Materiel: " + e.getMessage());
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