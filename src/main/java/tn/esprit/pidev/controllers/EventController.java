package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.services.EventService;
import tn.esprit.pidev.models.ApiResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EventController {
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private ListView<Evenement> eventListView;
    @FXML
    private ListView<Evenement> myEventsListView;
    
    private EventService eventService;
    private int currentUserId; // This should be set when user logs in

    @FXML
    public void initialize() {
        setupDatePicker();
        setupEventListView();
        eventService = new EventService();
        loadEvents();
        loadMyEvents();
    }

    private void setupDatePicker() {
        // Disable past dates
        eventDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        
                        if (date.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Light red for past dates
                        }
                    }
                };
            }
        });

        // Add value change listener
        eventDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isBefore(LocalDate.now())) {
                showAlert("Invalid Date", "Cannot select past dates", Alert.AlertType.WARNING);
                eventDatePicker.setValue(oldVal);
            }
        });
    }

    private void setupEventListView() {
        eventListView.setCellFactory(lv -> new ListCell<Evenement>() {
            private final Button joinButton = new Button("Join");
            private final HBox content = new HBox(10); // 10 is spacing
            private final VBox eventDetails = new VBox(5); // 5 is spacing

            {
                joinButton.setOnAction(e -> {
                    Evenement event = getItem();
                    if (event != null) {
                        joinEvent(event);
                    }
                });
            }

            @Override
            protected void updateItem(Evenement event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setGraphic(null);
                } else {
                    Label nameLabel = new Label(event.getNom());
                    Label dateLabel = new Label(event.getDateDebut().toString());
                    Label typeLabel = new Label(event.getType());
                    
                    // Style past events differently
                    if (event.getDateDebut().isBefore(LocalDateTime.now())) {
                        nameLabel.setTextFill(Color.RED);
                        joinButton.setDisable(true);
                    }
                    
                    eventDetails.getChildren().setAll(nameLabel, dateLabel, typeLabel);
                    content.getChildren().setAll(eventDetails, joinButton);
                    setGraphic(content);
                }
            }
        });
    }

    private void joinEvent(Evenement event) {
        if (event.getDateDebut().isBefore(LocalDateTime.now())) {
            showAlert("Cannot Join", "This event has already passed", Alert.AlertType.WARNING);
            return;
        }

        // Set default role as "participant"
        String userRole = "participant";
        
        ApiResponse<Boolean> response = eventService.joinEvent(currentUserId, event.getId().intValue(), userRole);
        if (response.isSuccess()) {
            showAlert("Success", "Successfully joined the event!", Alert.AlertType.INFORMATION);
            loadMyEvents(); // Refresh my events list
        } else {
            showAlert("Error", response.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadEvents() {
        var events = eventService.getAllEvents();
        eventListView.setItems(FXCollections.observableArrayList(events));
    }

    private void loadMyEvents() {
        var events = eventService.getMyEvents(currentUserId);
        eventListView.setItems(FXCollections.observableArrayList(events));
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to set current user ID (call this when user logs in)
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadEvents(); // Reload events for the new user
    }
} 