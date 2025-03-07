package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private void handleMaterielMenu(ActionEvent event) {
        loadModule(event, "/fxml/materiel-menu.fxml", "Material Management");
    }

    @FXML
    private void handleMaintenanceMenu(ActionEvent event) {
        loadModule(event, "/fxml/maintenance-menu.fxml", "Maintenance Management");
    }

    @FXML
    private void handleEmpruntMenu(ActionEvent event) {
        loadModule(event, "/fxml/emprunt-menu.fxml", "Loan Management");
    }

    @FXML
    private void handleStatisticsMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/statistics.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setTitle("Analytics Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading Analytics Dashboard: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadModule(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading " + title + ": " + ex.getMessage(), Alert.AlertType.ERROR);
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