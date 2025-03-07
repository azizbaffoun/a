package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import tn.esprit.pidev.models.Maintenance;
import tn.esprit.pidev.services.MaintenanceService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MaintenanceMenuController {
    private final MaintenanceService maintenanceService;

    public MaintenanceMenuController() {
        this.maintenanceService = new MaintenanceService();
    }

    @FXML
    private void handleAddMaintenance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/maintenance-add.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Maintenance");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading add maintenance page: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEditMaintenance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/maintenance-edit.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Edit Maintenance");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading edit maintenance page: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteMaintenance() {
        try {
            // First show a dialog to select the maintenance to delete
            List<Maintenance> maintenances = maintenanceService.getAll();
            if (maintenances.isEmpty()) {
                showAlert("Information", "No maintenance records found to delete.", Alert.AlertType.INFORMATION);
                return;
            }

            ChoiceDialog<Maintenance> dialog = new ChoiceDialog<>(maintenances.get(0), maintenances);
            dialog.setTitle("Select Maintenance");
            dialog.setHeaderText("Select a maintenance record to delete");
            dialog.setContentText("Choose maintenance:");

            dialog.showAndWait().ifPresent(maintenance -> {
                try {
                    // Show delete confirmation dialog
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeleteMaintenance.fxml"));
                    Parent root = loader.load();
                    
                    DeleteMaintenanceController controller = loader.getController();
                    controller.setMaintenanceId(maintenance.getMaintenanceID());
                    
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Delete Maintenance");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException ex) {
                    showAlert("Error", "Error loading delete maintenance page: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (SQLException ex) {
            showAlert("Error", "Error loading maintenance records: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackToMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setTitle("Sport Management System");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading main menu: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 