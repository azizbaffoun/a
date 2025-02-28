package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Text totalEventsText;
    @FXML private Text activeVenuesText;
    @FXML private Text totalUsersText;

    private Connection connection;
    private int currentUserId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
        loadStatistics();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    private void loadStatistics() {
        try {
            // Load total events
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evenement");
            if (rs.next()) {
                totalEventsText.setText(String.valueOf(rs.getInt(1)));
            }

            // Load active venues
            rs = stmt.executeQuery("SELECT COUNT(*) FROM terrain WHERE statut = 'Disponible'");
            if (rs.next()) {
                activeVenuesText.setText(String.valueOf(rs.getInt(1)));
            }

            // Load total users
            rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur");
            if (rs.next()) {
                totalUsersText.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            showError("Error loading statistics: " + e.getMessage());
        }
    }

    @FXML
    private void showEventManagement() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/EventManagement.fxml"));
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            showError("Error loading event management: " + e.getMessage());
        }
    }

    @FXML
    private void showVenueManagement() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/VenueManagement.fxml"));
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            showError("Error loading venue management: " + e.getMessage());
        }
    }

    @FXML
    private void showUserManagement() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserManagement.fxml"));
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            showError("Error loading user management: " + e.getMessage());
        }
    }

    @FXML
    private void showReports() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Reports.fxml"));
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            showError("Error loading reports: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 