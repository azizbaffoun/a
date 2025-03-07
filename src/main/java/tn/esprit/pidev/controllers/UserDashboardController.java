package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.ApiResponse;
import tn.esprit.pidev.services.EventService;
import tn.esprit.pidev.services.EventParticipantService;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.sql.Connection;

public class UserDashboardController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Text myEventsCount;
    @FXML private Text availableEventsCount;
    @FXML private ListView<Evenement> eventListView;
    @FXML private ListView<Evenement> myEventsListView;
    @FXML private Label eventDetailsLabel;

    private Connection connection;
    private EventService eventService;
    private EventParticipantService participantService;
    private int currentUserId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
        eventService = new EventService();
        participantService = new EventParticipantService();
        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            // Load events counts
            var myEventsResponse = eventService.getMyEvents(currentUserId);
            var allEvents = eventService.getAllEvents();
            myEventsCount.setText(String.valueOf(myEventsResponse.getData().size()));
            availableEventsCount.setText(String.valueOf(allEvents.size()));
        } catch (Exception e) {
            showError("Error loading dashboard data: " + e.getMessage());
        }
    }

    private void setupListViews() {
        if (eventListView != null) {
            eventListView.setCellFactory(lv -> new EventListCell());
            eventListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
                if (newValue != null) {
                    showEventDetails(newValue);
                }
            });
        }
        
        if (myEventsListView != null) {
            myEventsListView.setCellFactory(lv -> new EventListCell());
        }
    }

    private void showEventDetails(Evenement event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Event Details");
        dialog.setHeaderText(event.getNom());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("event-details-dialog");

        // Create styled labels for each detail
        Label typeLabel = new Label("Type: " + event.getType());
        typeLabel.getStyleClass().add("detail-label");

        Label descLabel = new Label("Description: " + event.getDescription());
        descLabel.getStyleClass().add("detail-label");
        descLabel.setWrapText(true);

        Label dateLabel = new Label(String.format("Date: %s - %s", 
            event.getDateDebut().toString(), 
            event.getDateFin().toString()));
        dateLabel.getStyleClass().add("detail-label");

        Label capacityLabel = new Label("Max Participants: " + event.getCapaciteMax());
        capacityLabel.getStyleClass().add("detail-label");

        content.getChildren().addAll(typeLabel, descLabel, dateLabel, capacityLabel);

        // Add a join button if the event is in the future
        if (!event.getDateDebut().isBefore(LocalDateTime.now())) {
            Button joinButton = new Button("Join Event");
            joinButton.getStyleClass().add("join-button");
            joinButton.setOnAction(e -> {
                handleJoinEvent();
                dialog.close();
            });
            content.getChildren().add(joinButton);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Add CSS styling
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        dialog.showAndWait();
    }

    @FXML
    private void handleJoinEvent() {
        Evenement selectedEvent = eventListView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Error", "Please select an event to join", Alert.AlertType.WARNING);
            return;
        }
        
        if (selectedEvent.getDateDebut().isBefore(LocalDateTime.now())) {
            showAlert("Error", "Cannot join past events", Alert.AlertType.WARNING);
            return;
        }
        
        // Set default role as "participant"
        String userRole = "participant";
        
        // Create join request with role
        ApiResponse<Boolean> response = eventService.joinEvent(currentUserId, selectedEvent.getId().intValue(), userRole);
        if (response.isSuccess()) {
            showAlert("Success", "Successfully joined the event!", Alert.AlertType.INFORMATION);
            loadDashboardData();
        } else {
            showAlert("Error", response.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadDashboardData();
    }

    @FXML
    private void showAvailableEvents() {
        contentArea.getChildren().clear();
        
        VBox container = new VBox(20);
        container.getStyleClass().add("content-container");
        container.setPadding(new Insets(20));
        
        // Header section with title and create button
        HBox headerSection = new HBox(20);
        headerSection.setAlignment(Pos.CENTER_LEFT);
        
        Text headerText = new Text("Available Events");
        headerText.getStyleClass().add("section-header");
        
        Button createEventButton = new Button("Create Event");
        createEventButton.getStyleClass().addAll("action-button", "create-button");
        createEventButton.setOnAction(e -> showCreateEventDialog());
        
        headerSection.getChildren().addAll(headerText, createEventButton);
        
        // Events list
        eventListView = new ListView<>();
        eventListView.getStyleClass().add("event-list");
        VBox.setVgrow(eventListView, Priority.ALWAYS);
        
        setupListViews();
        
        // Load all available events
        try {
            var events = eventService.getAllEvents();
            eventListView.setItems(FXCollections.observableArrayList(events));
        } catch (Exception e) {
            showError("Error loading events: " + e.getMessage());
        }
        
        container.getChildren().addAll(headerSection, eventListView);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(container);
    }

    private void showCreateEventDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateEvent.fxml"));
            Parent createEventView = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Create New Event");
            stage.setScene(new Scene(createEventView));
            
            // Get the controller and set necessary data
            CreateEventController controller = loader.getController();
            controller.setUserId(currentUserId);
            
            // When the create event window closes, refresh the events list
            stage.setOnHidden(e -> {
                loadDashboardData();
                showAvailableEvents();
            });
            
            stage.show();
        } catch (Exception e) {
            showError("Error opening create event form: " + e.getMessage());
        }
    }

    private class EventListCell extends ListCell<Evenement> {
        private final VBox content;
        private final Label nameLabel;
        private final Label dateLabel;
        private final Label typeLabel;
        private final Button joinButton;
        private final HBox buttonContainer;
        
        public EventListCell() {
            content = new VBox(5);
            content.setPadding(new Insets(10));
            content.getStyleClass().add("event-cell");
            
            nameLabel = new Label();
            nameLabel.getStyleClass().add("event-name");
            
            dateLabel = new Label();
            dateLabel.getStyleClass().add("event-date");
            
            typeLabel = new Label();
            typeLabel.getStyleClass().add("event-type");
            
            joinButton = new Button("Join Event");
            joinButton.getStyleClass().addAll("action-button", "join-button");
            joinButton.setOnAction(e -> handleJoinEvent());
            
            buttonContainer = new HBox(10);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
            buttonContainer.getChildren().add(joinButton);
            
            content.getChildren().addAll(nameLabel, dateLabel, typeLabel, buttonContainer);
        }
        
        @Override
        protected void updateItem(Evenement event, boolean empty) {
            super.updateItem(event, empty);
            
            if (empty || event == null) {
                setGraphic(null);
            } else {
                nameLabel.setText(event.getNom());
                dateLabel.setText("Date: " + event.getDateDebut().toString());
                typeLabel.setText("Type: " + event.getType());
                
                // Style and enable/disable join button based on event date
                boolean isPastEvent = event.getDateDebut().isBefore(LocalDateTime.now());
                joinButton.setDisable(isPastEvent);
                
                if (isPastEvent) {
                    nameLabel.setTextFill(Color.GRAY);
                    joinButton.setText("Past Event");
                } else {
                    nameLabel.setTextFill(Color.BLACK);
                    joinButton.setText("Join Event");
                }
                
                setGraphic(content);
            }
        }
    }

    @FXML
    private void showDashboard() {
        try {
            contentArea.getChildren().clear();
            
            VBox welcomeContainer = new VBox(30);
            welcomeContainer.setAlignment(Pos.CENTER);
            welcomeContainer.getStyleClass().add("welcome-container");
            
            Text welcomeText = new Text("Welcome to Your Dashboard");
            welcomeText.getStyleClass().add("welcome-text");
            
            Text subtitleText = new Text("Select an option from the sidebar to get started");
            subtitleText.getStyleClass().add("welcome-subtitle");
            
            HBox statsContainer = new HBox(30);
            statsContainer.setAlignment(Pos.CENTER);
            
            VBox eventsCard = createStatCard("📅", "My Events", myEventsCount);
            VBox availableCard = createStatCard("🎾", "Available Events", availableEventsCount);
            
            statsContainer.getChildren().addAll(eventsCard, availableCard);
            welcomeContainer.getChildren().addAll(welcomeText, subtitleText, statsContainer);
            
            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), welcomeContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            contentArea.getChildren().setAll(welcomeContainer);
            loadDashboardData();
        } catch (Exception e) {
            showError("Error showing dashboard: " + e.getMessage());
        }
    }

    private VBox createStatCard(String emoji, String label, Text valueText) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("stat-card");
        
        Text emojiText = new Text(emoji);
        emojiText.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 24px;");
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("stat-label");
        
        // Simpler hover effect using only opacity
        card.setOnMouseEntered(e -> {
            card.setEffect(new DropShadow(20, Color.valueOf("#5271ff")));
            FadeTransition ft = new FadeTransition(Duration.millis(200), card);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        });
        
        card.setOnMouseExited(e -> {
            card.setEffect(new DropShadow(10, Color.valueOf("#2d3545")));
            FadeTransition ft = new FadeTransition(Duration.millis(200), card);
            ft.setFromValue(1.0);
            ft.setToValue(0.8);
            ft.play();
        });
        
        card.getChildren().addAll(emojiText, labelText, valueText);
        return card;
    }

    @FXML
    private void showMyEvents() {
        contentArea.getChildren().clear();
        
        VBox container = new VBox(20);
        container.getStyleClass().add("content-container");
        container.setPadding(new Insets(20));
        
        Text headerText = new Text("My Events");
        headerText.getStyleClass().add("section-header");
        
        // Create ListView for my events
        myEventsListView = new ListView<>();
        myEventsListView.getStyleClass().add("event-list");
        VBox.setVgrow(myEventsListView, Priority.ALWAYS);
        
        // Setup the list view
        setupListViews();
        
        // Load my events
        loadDashboardData();
        
        container.getChildren().addAll(headerText, myEventsListView);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(container);
    }

    @FXML
    private void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserProfile.fxml"));
            Parent profileView = loader.load();
            
            // Clear and set new content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(profileView);
            
            // Get the controller and pass user ID if needed
            UserProfileController controller = loader.getController();
            controller.setUserId(currentUserId);
            
        } catch (Exception e) {
            showError("Error loading profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent loginView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) contentArea.getScene().getWindow();
            
            // Set the login scene
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
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