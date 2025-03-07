package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.net.URL;
import java.util.ResourceBundle;
import tn.esprit.pidev.models.User;
import tn.esprit.pidev.services.UserService;

public class UserProfileController implements Initializable {
    @FXML private VBox profileContainer;
    @FXML private ImageView profileImage;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button saveButton;
    
    private UserService userService;
    private int userId;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                usernameLabel.setText(user.getFullName());
                emailLabel.setText(user.getEmail());
                roleLabel.setText("Role ID: " + user.getRoleID());
                firstNameField.setText(user.getPrenom());
                lastNameField.setText(user.getNom());
                emailField.setText(user.getEmail());
                
                // Load profile photo if exists
                if (user.getPhotoProfil() != null && !user.getPhotoProfil().isEmpty()) {
                    try {
                        Image image = new Image(user.getPhotoProfil());
                        profileImage.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error loading profile image: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading user profile: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSave() {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setPrenom(firstNameField.getText());
                user.setNom(lastNameField.getText());
                user.setEmail(emailField.getText());
                userService.updateUser(user);
                loadUserProfile(); // Reload the profile
            }
        } catch (Exception e) {
            System.err.println("Error saving user profile: " + e.getMessage());
        }
    }
} 