package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import tn.esprit.pidev.services.EmpruntService;
import java.sql.SQLException;

public class DeleteEmpruntController {
    private EmpruntService service;
    private int empruntId; // ID of the Emprunt to delete

    public DeleteEmpruntController() {
        this.service = new EmpruntService();
    }

    @FXML
    public void handleDelete() {
        try {
            service.delete(empruntId);
            showAlert("Success", "Emprunt deleted successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete Emprunt: " + e.getMessage());
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