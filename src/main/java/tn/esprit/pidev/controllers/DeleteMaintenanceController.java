package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tn.esprit.pidev.services.MaintenanceService;
import java.sql.SQLException;

public class DeleteMaintenanceController {
    private MaintenanceService service;
    private int maintenanceId;
    
    @FXML
    private Button cancelButton;

    public DeleteMaintenanceController() {
        this.service = new MaintenanceService();
    }

    public void setMaintenanceId(int id) {
        this.maintenanceId = id;
    }

    @FXML
    public void handleDelete() {
        try {
            service.delete(maintenanceId);
            showAlert("Success", "Maintenance deleted successfully!", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete Maintenance: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 