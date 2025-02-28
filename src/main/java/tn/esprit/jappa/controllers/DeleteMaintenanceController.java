package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import tn.esprit.jappa.services.MaintenanceService;
import java.sql.SQLException;

public class DeleteMaintenanceController {
    private MaintenanceService service;
    private int maintenanceId; // ID of the Maintenance to delete

    public DeleteMaintenanceController() {
        this.service = new MaintenanceService();
    }

    @FXML
    public void handleDelete() {
        try {
            service.delete(maintenanceId);
            showAlert("Success", "Maintenance deleted successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete Maintenance: " + e.getMessage());
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