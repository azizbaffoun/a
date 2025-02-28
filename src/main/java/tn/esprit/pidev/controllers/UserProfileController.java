package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import tn.esprit.pidev.models.User;
import tn.esprit.pidev.services.UserService;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML private ImageView profilePhoto;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private TextField organizationField;

    private UserService userService;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
        loadUserProfile();
    }

    private void loadUserProfile() {
        // TODO: Get current user ID from session/login
        // For now, we'll use a placeholder
        currentUser = userService.getUserById(1); // Replace with actual user ID
        
        if (currentUser != null) {
            firstNameField.setText(currentUser.getPrenom());
            lastNameField.setText(currentUser.getNom());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getNumeroTelephone());
            addressArea.setText(currentUser.getAdresse());
            organizationField.setText(currentUser.getNomOrganisation());

            // Load profile photo if exists
            if (currentUser.getPhotoProfil() != null) {
                try {
                    Image image = new Image(new File(currentUser.getPhotoProfil()).toURI().toString());
                    profilePhoto.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading profile photo: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void updatePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profilePhoto.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                profilePhoto.setImage(image);
                
                if (userService.updateProfilePhoto(currentUser.getID(), selectedFile.getAbsolutePath())) {
                    showInfo("Profile photo updated successfully");
                } else {
                    showError("Failed to update profile photo");
                }
            } catch (Exception e) {
                showError("Error loading selected image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void saveChanges() {
        currentUser.setPrenom(firstNameField.getText());
        currentUser.setNom(lastNameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setNumeroTelephone(phoneField.getText());
        currentUser.setAdresse(addressArea.getText());
        currentUser.setNomOrganisation(organizationField.getText());

        if (userService.updateUser(currentUser)) {
            showInfo("Profile updated successfully");
        } else {
            showError("Failed to update profile");
        }
    }

    @FXML
    private void showChangePasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password");

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Current Password");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm New Password");

        dialog.getDialogPane().setContent(new VBox(10, 
            new Label("Current Password:"), currentPassword,
            new Label("New Password:"), newPassword,
            new Label("Confirm Password:"), confirmPassword
        ));

        ButtonType changeButton = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButton) {
                if (newPassword.getText().equals(confirmPassword.getText())) {
                    return newPassword.getText();
                }
                showError("New passwords do not match");
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPass -> {
            if (userService.resetPassword(currentUser.getID(), newPass)) {
                showInfo("Password changed successfully");
            } else {
                showError("Failed to change password");
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 