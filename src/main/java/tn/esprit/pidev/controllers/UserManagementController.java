package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import tn.esprit.pidev.models.User;
import tn.esprit.pidev.services.UserService;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserManagementController implements Initializable {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> genreColumn;
    @FXML private TableColumn<User, String> numeroTelephoneColumn;
    @FXML private TableColumn<User, String> adresseColumn;
    @FXML private TableColumn<User, Integer> roleIDColumn;
    @FXML private TableColumn<User, String> nomOrganisationColumn;
    @FXML private TableColumn<User, String> photoProfilColumn;
    @FXML private ComboBox<String> roleFilter;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private Label totalUsersLabel;

    private UserService userService;
    private ObservableList<User> userList;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
        setupTableColumns();
        setupRoleFilter();
        loadUsers();

        // Add search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        numeroTelephoneColumn.setCellValueFactory(new PropertyValueFactory<>("numeroTelephone"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        roleIDColumn.setCellValueFactory(new PropertyValueFactory<>("roleID"));
        nomOrganisationColumn.setCellValueFactory(new PropertyValueFactory<>("nomOrganisation"));
        photoProfilColumn.setCellValueFactory(new PropertyValueFactory<>("photoProfil"));
    }

    private void setupRoleFilter() {
        roleFilter.getItems().addAll("All", "1", "2"); // Admin and Regular User roles
        roleFilter.setValue("All");
        roleFilter.setOnAction(e -> filterUsers());
    }

    private void loadUsers() {
        userList = FXCollections.observableArrayList(userService.getAllUsers());
        userTable.setItems(userList);
        updateStatusBar();
    }

    private void filterUsers() {
        String role = roleFilter.getValue();
        String search = searchField.getText().toLowerCase();

        if (role.equals("All") && search.isEmpty()) {
            loadUsers();
        } else {
            userList = FXCollections.observableArrayList(
                userService.getAllUsers().stream()
                    .filter(u -> (role.equals("All") || String.valueOf(u.getRoleID()).equals(role)) &&
                               (search.isEmpty() || 
                                u.getNom().toLowerCase().contains(search) ||
                                u.getPrenom().toLowerCase().contains(search) ||
                                u.getEmail().toLowerCase().contains(search) ||
                                u.getNomOrganisation().toLowerCase().contains(search)))
                    .toList()
            );
            userTable.setItems(userList);
            updateStatusBar();
        }
    }

    @FXML
    private void handleSearch() {
        filterUsers();
    }

    @FXML
    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");

        GridPane grid = createUserDialogContent();
        dialog.getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                return getUserFromDialog(grid);
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            if (validateUser(user)) {
                if (userService.addUser(user)) {
                    loadUsers();
                    statusLabel.setText("User added successfully");
                } else {
                    showError("Failed to add user");
                }
            }
        });
    }

    @FXML
    private void showEditUserDialog() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Please select a user to edit");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user details");

        GridPane grid = createUserDialogContent();
        populateDialogWithUser(grid, selectedUser);
        dialog.getDialogPane().setContent(grid);

        ButtonType editButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == editButton) {
                User updatedUser = getUserFromDialog(grid);
                updatedUser.setID(selectedUser.getID());
                updatedUser.setMotdepasse(selectedUser.getMotdepasse()); // Preserve password
                return updatedUser;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            if (validateUser(user)) {
                if (userService.updateUser(user)) {
                    loadUsers();
                    statusLabel.setText("User updated successfully");
                } else {
                    showError("Failed to update user");
                }
            }
        });
    }

    @FXML
    private void showUpdatePhotoDialog() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Please select a user to update photo");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(userTable.getScene().getWindow());
        if (selectedFile != null) {
            String photoPath = selectedFile.getAbsolutePath();
            if (userService.updateProfilePhoto(selectedUser.getID(), photoPath)) {
                loadUsers();
                statusLabel.setText("Profile photo updated successfully");
            } else {
                showError("Failed to update profile photo");
            }
        }
    }

    @FXML
    private void deleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Please select a user to delete");
            return;
        }

        // Prevent deleting the last admin user
        if (selectedUser.getRoleID() == 1) {
            long adminCount = userList.stream()
                .filter(u -> u.getRoleID() == 1)
                .count();
            if (adminCount <= 1) {
                showError("Cannot delete the last administrator");
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userService.deleteUser(selectedUser.getID())) {
                loadUsers();
                showInfo("User deleted successfully");
            } else {
                showError("Failed to delete user");
            }
        }
    }

    @FXML
    private void showResetPasswordDialog() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Please select a user to reset password");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter new password for " + selectedUser.getFullName());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");
        newPassword.setStyle("-fx-font-family: 'Merriweather';");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm Password");
        confirmPassword.setStyle("-fx-font-family: 'Merriweather';");

        Label newPassLabel = new Label("New Password:");
        Label confirmPassLabel = new Label("Confirm Password:");
        newPassLabel.setStyle("-fx-font-family: 'Roboto'; -fx-font-weight: bold;");
        confirmPassLabel.setStyle("-fx-font-family: 'Roboto'; -fx-font-weight: bold;");

        grid.add(newPassLabel, 0, 0);
        grid.add(newPassword, 1, 0);
        grid.add(confirmPassLabel, 0, 1);
        grid.add(confirmPassword, 1, 1);

        // Make password fields take full width
        newPassword.setMaxWidth(Double.MAX_VALUE);
        confirmPassword.setMaxWidth(Double.MAX_VALUE);

        dialog.getDialogPane().setContent(grid);

        ButtonType resetButton = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButton, ButtonType.CANCEL);

        // Style the dialog buttons
        dialog.getDialogPane().lookupButton(resetButton)
              .setStyle("-fx-background-color: #00FF52; -fx-text-fill: white; " +
                       "-fx-font-family: 'Roboto'; -fx-font-size: 14px;");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL)
              .setStyle("-fx-background-color: #F2F2F2; -fx-text-fill: black; " +
                       "-fx-font-family: 'Roboto'; -fx-font-size: 14px;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButton) {
                String newPass = newPassword.getText();
                String confirmPass = confirmPassword.getText();
                
                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    showError("Password fields cannot be empty");
                    return null;
                }
                if (newPass.length() < 6) {
                    showError("Password must be at least 6 characters");
                    return null;
                }
                if (!newPass.equals(confirmPass)) {
                    showError("Passwords do not match");
                    return null;
                }
                return newPass;
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPass -> {
            if (userService.resetPassword(selectedUser.getID(), newPass)) {
                showInfo("Password reset successfully");
            } else {
                showError("Failed to reset password");
            }
        });
    }

    private GridPane createUserDialogContent() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        TextField prenomField = new TextField();
        prenomField.setPromptText("First Name");
        prenomField.setStyle("-fx-font-family: 'Merriweather';");

        TextField nomField = new TextField();
        nomField.setPromptText("Last Name");
        nomField.setStyle("-fx-font-family: 'Merriweather';");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-font-family: 'Merriweather';");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-family: 'Merriweather';");

        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll("Homme", "Femme");
        genreCombo.setPromptText("Gender");
        genreCombo.setStyle("-fx-font-family: 'Merriweather';");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle("-fx-font-family: 'Merriweather';");

        TextArea addressArea = new TextArea();
        addressArea.setPromptText("Address");
        addressArea.setPrefRowCount(2);
        addressArea.setStyle("-fx-font-family: 'Merriweather';");

        ComboBox<Integer> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(1, 2);
        roleCombo.setPromptText("Role");
        roleCombo.setStyle("-fx-font-family: 'Merriweather';");

        TextField orgField = new TextField();
        orgField.setPromptText("Organization Name");
        orgField.setStyle("-fx-font-family: 'Merriweather';");

        Label[] labels = {
            new Label("First Name:*"),
            new Label("Last Name:*"),
            new Label("Email:*"),
            new Label("Password:*"),
            new Label("Gender:"),
            new Label("Phone:*"),
            new Label("Address:"),
            new Label("Role:*"),
            new Label("Organization:")
        };

        for (Label label : labels) {
            label.setStyle("-fx-font-family: 'Roboto'; -fx-font-weight: bold;");
        }

        grid.add(labels[0], 0, 0);
        grid.add(prenomField, 1, 0);
        grid.add(labels[1], 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(labels[2], 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(labels[3], 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(labels[4], 0, 4);
        grid.add(genreCombo, 1, 4);
        grid.add(labels[5], 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(labels[6], 0, 6);
        grid.add(addressArea, 1, 6);
        grid.add(labels[7], 0, 7);
        grid.add(roleCombo, 1, 7);
        grid.add(labels[8], 0, 8);
        grid.add(orgField, 1, 8);

        // Make all fields take full width
        for (Node node : grid.getChildren()) {
            if (node instanceof Control) {
                ((Control) node).setMaxWidth(Double.MAX_VALUE);
            }
        }

        grid.getProperties().put("prenomField", prenomField);
        grid.getProperties().put("nomField", nomField);
        grid.getProperties().put("emailField", emailField);
        grid.getProperties().put("passwordField", passwordField);
        grid.getProperties().put("genreCombo", genreCombo);
        grid.getProperties().put("phoneField", phoneField);
        grid.getProperties().put("addressArea", addressArea);
        grid.getProperties().put("roleCombo", roleCombo);
        grid.getProperties().put("orgField", orgField);

        return grid;
    }

    private User getUserFromDialog(GridPane grid) {
        TextField prenomField = (TextField) grid.getProperties().get("prenomField");
        TextField nomField = (TextField) grid.getProperties().get("nomField");
        TextField emailField = (TextField) grid.getProperties().get("emailField");
        PasswordField passwordField = (PasswordField) grid.getProperties().get("passwordField");
        ComboBox<String> genreCombo = (ComboBox<String>) grid.getProperties().get("genreCombo");
        TextField phoneField = (TextField) grid.getProperties().get("phoneField");
        TextArea addressArea = (TextArea) grid.getProperties().get("addressArea");
        ComboBox<Integer> roleCombo = (ComboBox<Integer>) grid.getProperties().get("roleCombo");
        TextField orgField = (TextField) grid.getProperties().get("orgField");

        User user = new User();
        user.setPrenom(prenomField.getText());
        user.setNom(nomField.getText());
        user.setEmail(emailField.getText());
        user.setMotdepasse(passwordField.getText());
        user.setGenre(genreCombo.getValue());
        user.setNumeroTelephone(phoneField.getText());
        user.setAdresse(addressArea.getText());
        user.setRoleID(roleCombo.getValue());
        user.setNomOrganisation(orgField.getText());

        return user;
    }

    private void populateDialogWithUser(GridPane grid, User user) {
        TextField prenomField = (TextField) grid.getProperties().get("prenomField");
        TextField nomField = (TextField) grid.getProperties().get("nomField");
        TextField emailField = (TextField) grid.getProperties().get("emailField");
        ComboBox<String> genreCombo = (ComboBox<String>) grid.getProperties().get("genreCombo");
        TextField phoneField = (TextField) grid.getProperties().get("phoneField");
        TextArea addressArea = (TextArea) grid.getProperties().get("addressArea");
        ComboBox<Integer> roleCombo = (ComboBox<Integer>) grid.getProperties().get("roleCombo");
        TextField orgField = (TextField) grid.getProperties().get("orgField");

        prenomField.setText(user.getPrenom());
        nomField.setText(user.getNom());
        emailField.setText(user.getEmail());
        genreCombo.setValue(user.getGenre());
        phoneField.setText(user.getNumeroTelephone());
        addressArea.setText(user.getAdresse());
        roleCombo.setValue(user.getRoleID());
        orgField.setText(user.getNomOrganisation());

        // Hide password field for editing
        grid.getChildren().removeIf(node -> 
            GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == 3);
    }

    private boolean validateUser(User user) {
        StringBuilder errors = new StringBuilder();

        if (user.getPrenom() == null || user.getPrenom().trim().isEmpty()) {
            errors.append("First Name is required\n");
        }
        if (user.getNom() == null || user.getNom().trim().isEmpty()) {
            errors.append("Last Name is required\n");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.append("Email is required\n");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.append("Invalid email format\n");
        }
        if (user.getMotdepasse() != null && !user.getMotdepasse().trim().isEmpty() && 
            user.getMotdepasse().length() < 6) {
            errors.append("Password must be at least 6 characters\n");
        }
        if (user.getNumeroTelephone() == null || user.getNumeroTelephone().trim().isEmpty()) {
            errors.append("Phone number is required\n");
        } else if (!PHONE_PATTERN.matcher(user.getNumeroTelephone()).matches()) {
            errors.append("Invalid phone number format (must be 8 digits)\n");
        }
        if (user.getRoleID() == 0) {
            errors.append("Role is required\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        return true;
    }

    private void updateStatusBar() {
        totalUsersLabel.setText("Total Users: " + userList.size());
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