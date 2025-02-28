package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MaintenanceMenuController {

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/maintenance-delete.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Delete Maintenance");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading delete maintenance page: " + ex.getMessage(), Alert.AlertType.ERROR);
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