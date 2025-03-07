package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.geometry.Pos;

public class AdminDashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label totalEventsText;
    @FXML private Label activeVenuesText;
    @FXML private Label totalUsersText;
    @FXML private Label revenueText;

    private Connection connection;
    private int currentUserId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DatabaseConnection.closeConnection(); // Close any existing connection
        connection = DatabaseConnection.getConnection(); // Get a fresh connection
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

            // Load revenue from tickets (billets)
            rs = stmt.executeQuery(
                "SELECT COALESCE(SUM(b.prix * rb.nombreBillet), 0) as total_revenue " +
                "FROM billet b " +
                "LEFT JOIN reservation_billet rb ON b.ID = rb.billetID " +
                "LEFT JOIN reservation r ON rb.reservationID = r.ID " +
                "WHERE r.statut = 'Confirmée'"
            );
            if (rs.next()) {
                double totalRevenue = rs.getDouble("total_revenue");
                revenueText.setText(String.format("%.2f DT", totalRevenue));
            }

        } catch (Exception e) {
            showError("Error loading statistics: " + e.getMessage());
        }
    }

    @FXML
    private void showDashboard() {
        try {
            // Clear existing content
            contentArea.getChildren().clear();
            
            // Create welcome container
            VBox welcomeContainer = new VBox(30);
            welcomeContainer.setAlignment(Pos.CENTER);
            welcomeContainer.getStyleClass().add("welcome-container");
            
            // Add welcome text
            Text welcomeText = new Text("Welcome to Admin Dashboard");
            welcomeText.getStyleClass().add("welcome-text");
            
            Text subtitleText = new Text("Select an option from the sidebar to get started");
            subtitleText.getStyleClass().add("welcome-subtitle");
            
            // Create statistics container
            HBox statsContainer = new HBox(30);
            statsContainer.setAlignment(Pos.CENTER);
            
            // Add statistics cards
            VBox eventsCard = createStatCard("E", "Total Events", totalEventsText);
            VBox venuesCard = createStatCard("V", "Active Venues", activeVenuesText);
            VBox usersCard = createStatCard("U", "Total Users", totalUsersText);
            VBox revenueCard = createStatCard("R", "Revenue", revenueText);
            
            statsContainer.getChildren().addAll(eventsCard, venuesCard, usersCard, revenueCard);
            welcomeContainer.getChildren().addAll(welcomeText, subtitleText, statsContainer);
            
            // Add to content area
            contentArea.getChildren().setAll(welcomeContainer);
            
            // Refresh statistics
            loadStatistics();
        } catch (Exception e) {
            showError("Error showing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createStatCard(String icon, String label, Label valueText) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("stat-card");
        
        Text iconText = new Text(icon);
        iconText.getStyleClass().add("emoji-icon");
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("stat-label");
        
        card.getChildren().addAll(iconText, labelText, valueText);
        return card;
    }

    @FXML
    private void showEventManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventManagement.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            showError("Error loading event management: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showVenueManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VenueManagement.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            showError("Error loading venue management: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserManagement.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            showError("Error loading user management: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showReports() {
        // TODO: Implement reports view
        showMessage("Reports view coming soon!");
    }

    @FXML
    private void showApiManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ApiManagement.fxml"));
            Parent apiManagementView = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(apiManagementView);
        } catch (IOException e) {
            showError("Error loading API Management view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 