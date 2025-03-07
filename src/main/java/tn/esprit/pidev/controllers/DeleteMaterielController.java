package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import tn.esprit.pidev.services.MaterielService;
import java.sql.SQLException;

public class DeleteMaterielController {
    private MaterielService service;
    private int materielId; // ID of the Materiel to delete

    public DeleteMaterielController() {
        this.service = new MaterielService();
    }

    @FXML
    public void handleDelete() {
        try {
            service.delete(materielId);
            showAlert("Success", "Materiel deleted successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete Materiel: " + e.getMessage());
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