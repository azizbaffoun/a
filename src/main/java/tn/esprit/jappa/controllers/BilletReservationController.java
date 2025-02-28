package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import tn.esprit.jappa.models.Billet;
import tn.esprit.jappa.models.Event;
import tn.esprit.jappa.services.BilletService;
import tn.esprit.jappa.services.EventService;
import tn.esprit.jappa.services.TwilioService;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class BilletReservationController implements Initializable {
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private ComboBox<Billet> ticketComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label availableQuantityLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label priceLabel;
    @FXML private Button reserveButton;
    @FXML private ListView<Billet> reservationsListView;
    @FXML private TextField phoneNumberField;
    @FXML private ComboBox<String> countryCodeCombo;

    private BilletService billetService;
    private EventService eventService;
    private TwilioService twilioService;
    private ObservableList<Event> eventList;
    private ObservableList<Billet> availableTickets;
    private ObservableList<Billet> userReservations;
    private double totalPrice = 0.0;
    private String pendingPhoneNumber;
    private final Map<String, String> countryCodes = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            billetService = new BilletService();
            eventService = new EventService();
            twilioService = new TwilioService();
            setupInitialState();
            setupListeners();
            initializeCountryCodes();
            countryCodeCombo.getItems().addAll(countryCodes.keySet());
            countryCodeCombo.setValue("+216"); // Tunisia by default
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error initializing services: " + e.getMessage());
        }
    }

    private void setupInitialState() throws SQLException {
        // Initialize lists
        eventList = FXCollections.observableArrayList();
        availableTickets = FXCollections.observableArrayList();
        userReservations = FXCollections.observableArrayList();
        
        // Load events
        eventList.addAll(eventService.getAll());
        eventComboBox.setItems(eventList);
        
        // Setup quantity spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);
        quantitySpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            try {
                int value = Integer.parseInt(newValue);
                Billet selectedTicket = ticketComboBox.getValue();
                if (selectedTicket != null) {
                    try {
                        int availableQuantity = billetService.getAvailableQuantity(selectedTicket.getId());
                        if (value < 1) {
                            quantitySpinner.getValueFactory().setValue(1);
                        } else if (value > availableQuantity) {
                            quantitySpinner.getValueFactory().setValue(availableQuantity);
                        }
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to get available quantity: " + e.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
                quantitySpinner.getEditor().setText(oldValue);
            }
        });
        
        // Setup lists
        reservationsListView.setItems(userReservations);
        ticketComboBox.setItems(availableTickets);
        
        // Initial labels
        totalPriceLabel.setText("Total: 0.00€");
        availableQuantityLabel.setText("Available: 0");
        
        // Load user reservations
        refreshReservations();
    }

    private void setupListeners() {
        eventComboBox.setOnAction(event -> {
            Event selectedEvent = eventComboBox.getValue();
            if (selectedEvent != null) {
                try {
                    availableTickets.clear();
                    availableTickets.addAll(billetService.getTicketsByEvent(selectedEvent.getId()));
                    setupDatePickerForEvent(selectedEvent);
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tickets: " + e.getMessage());
                }
            }
        });

        ticketComboBox.setOnAction(event -> {
            Billet selectedTicket = ticketComboBox.getValue();
            if (selectedTicket != null) {
                try {
                    int availableQuantity = billetService.getAvailableQuantity(selectedTicket.getId());
                    availableQuantityLabel.setText("Available: " + availableQuantity);
                    updateTotalPrice();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to get available quantity: " + e.getMessage());
                }
            }
        });

        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTotalPrice();
        });
    }

    private void setupDatePickerForEvent(Event event) {
        if (event != null) {
            LocalDate startDate = event.getDateDebut();
            LocalDate endDate = event.getDateFin();
            
            datePicker.setValue(startDate);
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(startDate) < 0 || date.compareTo(endDate) > 0);
                }
            });
        }
    }

    private void updateTotalPrice() {
        Billet selectedTicket = ticketComboBox.getValue();
        if (selectedTicket != null) {
            int quantity = quantitySpinner.getValue();
            totalPrice = selectedTicket.getPrix() * quantity;
            totalPriceLabel.setText(String.format("Total: %.2f€", totalPrice));
        }
    }

    private void refreshReservations() {
        try {
            userReservations.clear();
            userReservations.addAll(billetService.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reservations: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void initializeCountryCodes() {
        countryCodes.put("+216", "TN"); // Tunisia
        countryCodes.put("+213", "DZ"); // Algeria
        countryCodes.put("+212", "MA"); // Morocco
        countryCodes.put("+218", "LY"); // Libya
        countryCodes.put("+20", "EG");  // Egypt
        countryCodes.put("+966", "SA"); // Saudi Arabia
        countryCodes.put("+971", "AE"); // UAE
        countryCodes.put("+974", "QA"); // Qatar
        countryCodes.put("+973", "BH"); // Bahrain
        countryCodes.put("+965", "KW"); // Kuwait
    }

    private String formatPhoneNumber(String phoneNumber) {
        String countryCode = countryCodeCombo.getValue();
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        return countryCode + phoneNumber.replaceAll("[^0-9]", "");
    }

    @FXML
    private void handleReservation() {
        Billet selectedTicket = ticketComboBox.getValue();
        if (selectedTicket == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a ticket!");
            return;
        }

        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a date!");
            return;
        }

        Event selectedEvent = eventComboBox.getValue();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an event!");
            return;
        }

        if (selectedDate.isBefore(selectedEvent.getDateDebut()) || selectedDate.isAfter(selectedEvent.getDateFin())) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Selected date must be between event start and end dates!");
            return;
        }

        String phoneNumber = phoneNumberField.getText();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a phone number");
            return;
        }

        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        pendingPhoneNumber = formattedPhoneNumber;
        
        if (twilioService.sendVerificationCode(formattedPhoneNumber)) {
            showVerificationDialog();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send verification code");
        }
    }

    private void showVerificationDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Phone Verification");
        dialog.setHeaderText("Enter the verification code sent to your phone");

        ButtonType verifyButtonType = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(verifyButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField codeField = new TextField();
        codeField.setPromptText("Enter verification code");
        content.getChildren().add(codeField);

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == verifyButtonType) {
                return codeField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            if (twilioService.verifyCode(pendingPhoneNumber, code)) {
                completeReservation();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid verification code. Please try again.");
            }
        });
    }

    private void completeReservation() {
        int quantity = quantitySpinner.getValue();
        try {
            int availableQuantity = billetService.getAvailableQuantity(ticketComboBox.getValue().getId());
            if (quantity > availableQuantity) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Not enough tickets available!");
                return;
            }

            // Update ticket quantity
            if (billetService.updateQuantity(ticketComboBox.getValue().getId(), -quantity)) {
                // Update available quantity label
                availableQuantity = billetService.getAvailableQuantity(ticketComboBox.getValue().getId());
                availableQuantityLabel.setText("Available: " + availableQuantity);
                
                // Update spinner max value
                SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, availableQuantity, 1);
                quantitySpinner.setValueFactory(valueFactory);
                
                // Refresh tickets in combo box
                availableTickets.clear();
                availableTickets.addAll(billetService.getTicketsByEvent(eventComboBox.getValue().getId()));
                
                refreshReservations();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation successful!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to make reservation!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to make reservation: " + e.getMessage());
        }
    }

    private void clearFields() {
        eventComboBox.setValue(null);
        ticketComboBox.setValue(null);
        datePicker.setValue(null);
        quantitySpinner.getValueFactory().setValue(1);
        availableQuantityLabel.setText("Available: 0");
        totalPriceLabel.setText("Total: 0.00€");
    }
} 