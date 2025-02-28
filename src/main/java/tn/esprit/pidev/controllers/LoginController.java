package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private Connection connection;
    private int userID; // Add userID as class field

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
    }

    @FXML
    private void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password");
            return;
        }

        try {
            // Query to check credentials with plain text password
            String query = "SELECT * FROM utilisateur WHERE email = ? AND motdepasse = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);  // Using plain text password

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Get the user's role from the database
                int roleID = rs.getInt("roleID");
                
                // Store the user ID in a static variable or session for later use
                userID = rs.getInt("ID"); // Update class field instead of local variable
                
                // Login successful - direct to appropriate dashboard based on role
                if (roleID == 1) { // Admin role
                    loadAdminDashboard();
                } else if (roleID == 2) { // Regular user role
                    loadUserDashboard();
                } else {
                    errorLabel.setText("Invalid role type");
                }
            } else {
                errorLabel.setText("Invalid email or password");
                // Clear password field for security
                passwordField.clear();
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
            // Clear password field for security
            passwordField.clear();
        }
    }
    // kenou adimin yloadi interface mta3admin

    private void loadAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the user ID
            AdminDashboardController controller = loader.getController();
            controller.setCurrentUserId(userID);
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Admin Dashboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error loading admin dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUserDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the user ID
            UserDashboardController controller = loader.getController();
            controller.setCurrentUserId(userID);
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("User Dashboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error loading user dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 