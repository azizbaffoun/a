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

public class EmpruntMenuController {

    @FXML
    private void handleAddEmprunt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/emprunt-add.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Loan");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading add loan page: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleManageEmprunt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/emprunt-manage.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Manage Loans");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Error", "Error loading manage loans page: " + ex.getMessage(), Alert.AlertType.ERROR);
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