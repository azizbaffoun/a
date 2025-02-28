package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import tn.esprit.jappa.models.*;
import tn.esprit.jappa.services.*;
import tn.esprit.jappa.utils.DatabaseConnection;
import tn.esprit.jappa.exceptions.SmsVerificationException;
import com.twilio.exception.ApiException;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;

public class ReservationBilletController implements Initializable {
    @FXML private ComboBox<String> userComboBox;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<ReservationStatus> statutComboBox;
    @FXML private ListView<Reservation> reservationListView;
    @FXML private ComboBox<Billet> billetComboBox;
    @FXML private TextField nombreBilletField;
    @FXML private TextField phoneNumberField;

    private ReservationService reservationService;
    private ObservableList<Reservation> reservationList;
    private BilletService billetService;
    private SmsVerificationService smsVerificationService;
    private String pendingPhoneNumber;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            reservationService = new ReservationService();
            billetService = new BilletService();
            smsVerificationService = new SmsVerificationService();
            reservationList = FXCollections.observableArrayList();

            // Initialize status ComboBox with enum values
            statutComboBox.setItems(FXCollections.observableArrayList(ReservationStatus.values()));

            refreshList();
            loadUsers();
            loadBillets();

            reservationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    userComboBox.getItems().stream()
                        .filter(item -> item.endsWith("(" + newSelection.getUtilisateurID() + ")"))
                        .findFirst()
                        .ifPresent(user -> userComboBox.setValue(user));
                    
                    dateReservationPicker.setValue(newSelection.getDateReservation());
                    statutComboBox.setValue(ReservationStatus.fromString(newSelection.getStatut()));
                }
            });

            // Add listener to billetComboBox to update datePicker
            billetComboBox.valueProperty().addListener((obs, oldBillet, newBillet) -> {
                if (newBillet != null) {
                    updateDatePicker(newBillet);
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error initializing services: " + e.getMessage());
        }
    }

    private void updateDatePicker(Billet billet) {
        Event associatedEvent = getEventById(billet.getEventID());
        if (associatedEvent != null) {
            LocalDate startDate = associatedEvent.getDateDebut();
            LocalDate endDate = associatedEvent.getDateFin();

            dateReservationPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    // Disable dates outside the event's date range
                    if (item.isBefore(startDate) || item.isAfter(endDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: red;"); // Optional: Change color for unavailable dates
                    }
                }
            });
        }
    }

    private void loadUsers() {
        String query = "SELECT ID, prenom, nom FROM utilisateur";
        ObservableList<String> users = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                String userDisplay = String.format("%s %s (%d)", 
                    rs.getString("prenom"),
                    rs.getString("nom"),
                    rs.getInt("ID"));
                users.add(userDisplay);
            }
            
            userComboBox.setItems(users);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading users: " + e.getMessage());
        }
    }

    private void loadBillets() {
        try {
            List<Billet> billets = billetService.getAll();
            billetComboBox.setItems(FXCollections.observableArrayList(billets));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading billets: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) return;

        try {
            String userSelection = userComboBox.getValue();
            if (userSelection == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user!");
                return;
            }
            
            // Extract ID from the format "prenom nom (ID)"
            int userId = Integer.parseInt(userSelection.substring(
                userSelection.lastIndexOf("(") + 1,
                userSelection.lastIndexOf(")")
            ));

            Billet selectedBillet = billetComboBox.getValue();
            if (selectedBillet == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a ticket!");
                return;
            }
            
            // Validate quantity against selected billet
            int quantity = Integer.parseInt(nombreBilletField.getText());
            if (quantity > selectedBillet.getQuantite()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Quantity cannot exceed available tickets!");
                return;
            }

            // Validate phone number and send verification code
            String phoneNumber = phoneNumberField.getText();
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a phone number!");
                return;
            }

            // Store data for verification callback
            pendingPhoneNumber = phoneNumber;
            
            try {
                // Send verification code
                smsVerificationService.sendVerificationCode(phoneNumber);
                
                // Show verification dialog
                showVerificationDialog();
            } catch (SmsVerificationException e) {
                showAlert(Alert.AlertType.ERROR, "SMS Verification Error", e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format: " + e.getMessage());
        }
    }

    private void showVerificationDialog() {
        // Create dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Verify Phone Number");
        dialog.setHeaderText("Enter the verification code sent to your phone");

        // Set the button types
        ButtonType verifyButtonType = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(verifyButtonType, ButtonType.CANCEL);

        // Create the verification code field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField verificationCode = new TextField();
        verificationCode.setPromptText("Enter code");

        grid.add(new Label("Verification code:"), 0, 0);
        grid.add(verificationCode, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a string when the verify button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == verifyButtonType) {
                return verificationCode.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            try {
                if (smsVerificationService.verifyCode(pendingPhoneNumber, code)) {
                    completeReservation();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid verification code!");
                }
            } catch (SmsVerificationException e) {
                showAlert(Alert.AlertType.ERROR, "SMS Verification Error", e.getMessage());
            }
        });
    }

    private void completeReservation() {
        try {
            String userSelection = userComboBox.getValue();
            int userId = Integer.parseInt(userSelection.substring(
                userSelection.lastIndexOf("(") + 1,
                userSelection.lastIndexOf(")")
            ));

            Billet selectedBillet = billetComboBox.getValue();
            int quantity = Integer.parseInt(nombreBilletField.getText());

            // Create Reservation
            Reservation reservation = new Reservation(
                userId,
                dateReservationPicker.getValue(),
                statutComboBox.getValue().getDisplayName()
            );
            int reservationId = reservationService.add(reservation);

            // Create ReservationBillet
            ReservationBillet reservationBillet = new ReservationBillet(
                selectedBillet.getId(),
                quantity,
                selectedBillet
            );
            reservationBillet.setReservationID(reservationId);

            // Add reservation billet
            ReservationBilletService reservationBilletService = new ReservationBilletService();
            reservationBilletService.add(reservationBillet);

            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation completed successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error completing reservation: " + e.getMessage());
        }
    }

    private Event getEventById(int eventId) {
        try {
            return billetService.getEventById(eventId); // Assuming you have this method in BilletService
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error fetching event: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleUpdate() {
        Reservation selectedReservation = reservationListView.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reservation to update!");
            return;
        }

        if (!validateInput()) return;

        try {
            String userSelection = userComboBox.getValue();
            if (userSelection == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user!");
                return;
            }
            
            // Extract ID from the format "prenom nom (ID)"
            int userId = Integer.parseInt(userSelection.substring(
                userSelection.lastIndexOf("(") + 1,
                userSelection.lastIndexOf(")")
            ));

            selectedReservation.setUtilisateurID(userId);
            selectedReservation.setDateReservation(dateReservationPicker.getValue());
            selectedReservation.setStatut(statutComboBox.getValue().getDisplayName());
            reservationService.update(selectedReservation);

            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation updated successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating reservation: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Reservation selectedReservation = reservationListView.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reservation to delete!");
            return;
        }

        try {
            reservationService.delete(selectedReservation.getId());
            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation deleted successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error deleting reservation: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        // Validate User Selection
        if (userComboBox.getValue() == null) {
            errors.append("Please select a user\n");
        }

        // Validate Date
        if (dateReservationPicker.getValue() == null) {
            errors.append("Reservation date is required\n");
        }

        // Validate Status
        if (statutComboBox.getValue() == null) {
            errors.append("Status is required\n");
        }

        // Validate Ticket Quantity
        if (nombreBilletField.getText().isEmpty()) {
            errors.append("Number of tickets is required\n");
        } else {
            try {
                int quantity = Integer.parseInt(nombreBilletField.getText());
                if (quantity <= 0) {
                    errors.append("Number of tickets must be greater than zero\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Invalid number format for tickets\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errors.toString());
            return false;
        }
        return true;
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void refreshList() {
        try {
            reservationList.clear();
            List<Reservation> reservations = reservationService.getAll();
            
            reservations.stream()
                       .sorted((r1, r2) -> r2.getId() - r1.getId())
                       .forEach(reservationList::add);
                       
            reservationListView.setItems(reservationList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error refreshing list: " + e.getMessage());
        }
    }

    private void clearFields() {
        userComboBox.setValue(null);
        dateReservationPicker.setValue(null);
        statutComboBox.setValue(null);
        reservationListView.getSelectionModel().clearSelection();
        billetComboBox.setValue(null);
        nombreBilletField.clear();
        phoneNumberField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 