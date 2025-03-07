package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class MaterielMenuController {

    @FXML
    private void handleAddMaterial(ActionEvent event) {
        loadModule(event, "/fxml/materiel-add.fxml", "Add Material");
    }

    @FXML
    private void handleEditMaterial(ActionEvent event) {
        loadModule(event, "/fxml/materiel-manage.fxml", "Edit Materials");
    }

    @FXML
    private void handleDeleteMaterial(ActionEvent event) {
        loadModule(event, "/fxml/materiel-delete.fxml", "Delete Materials");
    }

    @FXML
    private void handleQRCodeManagement(ActionEvent event) {
        loadModule(event, "/fxml/qr-code-management.fxml", "QR Code Management");
    }

    @FXML
    private void handleBackToMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setTitle("Main Menu");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Error loading Main Menu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadModule(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Create a new stage
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Error loading " + title + ": " + e.getMessage(), Alert.AlertType.ERROR);
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