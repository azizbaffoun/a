package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.jappa.models.Billet;
import tn.esprit.jappa.models.Event;
import tn.esprit.jappa.models.BilletStatus;
import tn.esprit.jappa.services.BilletService;
import tn.esprit.jappa.services.EventService;
import tn.esprit.jappa.services.UserService;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BilletController implements Initializable {
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private ComboBox<BilletStatus> statutComboBox;
    @FXML private ListView<Billet> billetListView;
    @FXML private DatePicker dateAchatPicker;
    @FXML private TextField prixField;
    @FXML private TextField typeBilletField;
    @FXML private Spinner<Integer> quantiteSpinner;

    private BilletService billetService;
    private EventService eventService;
    private UserService userService;
    private ObservableList<Billet> billetList;
    private ObservableList<Event> eventList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            billetService = new BilletService();
            eventService = new EventService();
            setupInitialState();
            setupEventHandlers();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error initializing services: " + e.getMessage());
        }
    }

    private void setupInitialState() throws SQLException {
        // Initialize services
        userService = new UserService();
        
        // Initialize lists
        billetList = FXCollections.observableArrayList();
        eventList = FXCollections.observableArrayList();
        
        // Load events into combo box
        eventList.addAll(eventService.getAll());
        eventComboBox.setItems(eventList);
        
        // Setup statut combo box
        statutComboBox.setItems(FXCollections.observableArrayList(BilletStatus.values()));
        statutComboBox.setValue(BilletStatus.VALIDE);
        
        // Setup quantity spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
        quantiteSpinner.setValueFactory(valueFactory);
        quantiteSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            try {
                int value = Integer.parseInt(newValue);
                if (value < 1) {
                    quantiteSpinner.getValueFactory().setValue(1);
                } else if (value > 1000) {
                    quantiteSpinner.getValueFactory().setValue(1000);
                }
            } catch (NumberFormatException e) {
                quantiteSpinner.getEditor().setText(oldValue);
            }
        });
        
        // Setup date picker
        dateAchatPicker.setEditable(false);
        
        // Setup list view
        billetListView.setItems(billetList);
        billetListView.setCellFactory(lv -> new ListCell<Billet>() {
            @Override
            protected void updateItem(Billet billet, boolean empty) {
                super.updateItem(billet, empty);
                if (empty || billet == null) {
                    setText(null);
                } else {
                    Event event = billet.getEvent();
                    String eventName = event != null ? event.getNom() : "Unknown Event";
                    setText(String.format("%s - %s - Price: %.2f€ - Quantity: %d - Status: %s", 
                        eventName,
                        billet.getTypeBillet(),
                        billet.getPrix(),
                        billet.getQuantite(),
                        billet.getStatut()
                    ));
                }
            }
        });
        
        // Load tickets
        refreshList();
    }

    private void setupEventHandlers() {
        eventComboBox.setOnAction(event -> {
            Event selectedEvent = eventComboBox.getValue();
            if (selectedEvent != null) {
                try {
                    billetList.clear();
                    billetList.addAll(billetService.getTicketsByEvent(selectedEvent.getId()));
                    setupDatePickerForEvent(selectedEvent);
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tickets: " + e.getMessage());
                }
            }
        });

        // Add listener for price validation
        prixField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                prixField.setText(oldVal);
            }
        });

        // Add listener for type validation
        typeBilletField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 255) {
                typeBilletField.setText(oldVal);
            }
        });

        // Add listener for quantity validation
        quantiteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal < 1) {
                quantiteSpinner.getValueFactory().setValue(1);
            }
        });

        // Add listener for ticket selection
        billetListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                eventComboBox.setValue(newVal.getEvent());
                dateAchatPicker.setValue(newVal.getDateAchat());
                prixField.setText(String.format("%.2f", newVal.getPrix()));
                typeBilletField.setText(newVal.getTypeBillet());
                statutComboBox.setValue(BilletStatus.valueOf(newVal.getStatut()));
                quantiteSpinner.getValueFactory().setValue(newVal.getQuantite());
            }
        });
    }

    private void setupDatePickerForEvent(Event event) {
        dateAchatPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                
                if (empty || date == null) {
                    setDisable(true);
                    return;
                }
                
                LocalDate eventStart = event.getDateDebut();
                LocalDate eventEnd = event.getDateFin();
                
                boolean isValidDate = !date.isBefore(eventStart) && !date.isAfter(eventEnd);
                
                setDisable(!isValidDate);
                setStyle(isValidDate ? "" : "-fx-background-color: #ffd4d4;");
            }
        });
        
        // Set default value to event start date
        dateAchatPicker.setValue(event.getDateDebut());
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        
        if (eventComboBox.getValue() == null) {
            errors.append("Please select an event\n");
        }
        
        if (dateAchatPicker.getValue() == null) {
            errors.append("Please select a purchase date\n");
        }
        
        if (prixField.getText().isEmpty() || !prixField.getText().matches("\\d*\\.?\\d*")) {
            errors.append("Please enter a valid price\n");
        }
        
        if (typeBilletField.getText().isEmpty() || typeBilletField.getText().length() > 255) {
            errors.append("Please enter a valid ticket type (max 255 characters)\n");
        }
        
        if (statutComboBox.getValue() == null) {
            errors.append("Please select a status\n");
        }
        
        if (quantiteSpinner.getValue() == null || quantiteSpinner.getValue() < 1) {
            errors.append("Please enter a valid quantity (minimum 1)\n");
        }
        
        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errors.toString());
            return false;
        }
        
        return true;
    }

    @FXML
    private void handleAdd() {
        if (!validateInputs()) {
            return;
        }

        try {
            Event selectedEvent = eventComboBox.getValue();
            Billet billet = new Billet(
                selectedEvent.getId(),
                dateAchatPicker.getValue(),
                Double.parseDouble(prixField.getText()),
                typeBilletField.getText(),
                statutComboBox.getValue().name(),
                quantiteSpinner.getValue()
            );
            
            billetService.add(billet);
            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket added successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add ticket: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid price format: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Billet selectedTicket = billetListView.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a ticket to update!");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try {
            Event selectedEvent = eventComboBox.getValue();
            selectedTicket.setEventID(selectedEvent.getId());
            selectedTicket.setDateAchat(dateAchatPicker.getValue());
            selectedTicket.setPrix(Double.parseDouble(prixField.getText()));
            selectedTicket.setTypeBillet(typeBilletField.getText());
            selectedTicket.setStatut(statutComboBox.getValue().name());
            selectedTicket.setQuantite(quantiteSpinner.getValue());
            
            billetService.update(selectedTicket);
            refreshList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket updated successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update ticket: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid price format: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Billet selectedTicket = billetListView.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a ticket to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setContentText("Are you sure you want to delete this ticket?");
        
        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                billetService.delete(selectedTicket.getId());
                refreshList();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket deleted successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete ticket: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void clearFields() {
        eventComboBox.setValue(null);
        dateAchatPicker.setValue(null);
        prixField.clear();
        typeBilletField.clear();
        statutComboBox.setValue(BilletStatus.VALIDE);
        quantiteSpinner.getValueFactory().setValue(1);
    }

    private void refreshList() {
        try {
            billetList.clear();
            billetList.addAll(billetService.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tickets: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setBilletService(BilletService billetService) {
        this.billetService = billetService;
        try {
            setupInitialState();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to initialize: " + e.getMessage());
        }
    }
} 
