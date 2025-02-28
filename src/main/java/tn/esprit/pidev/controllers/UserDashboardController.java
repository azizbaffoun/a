package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;

import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.ApiResponse;
import tn.esprit.pidev.models.Venue;
import tn.esprit.pidev.services.EventService;
import tn.esprit.pidev.services.EventParticipantService;
import tn.esprit.pidev.services.VenueService;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ArrayList;

public class UserDashboardController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Text myEventsCount;
    @FXML private Text availableEventsCount;
    @FXML private ListView<Evenement> eventListView;
    @FXML private ListView<Evenement> myEventsListView;
    @FXML private Label eventDetailsLabel;

    private EventService eventService;
    private EventParticipantService participantService;
    private VenueService venueService;
    private int currentUserId;

    private static final String CALORIES_API_KEY = "D+op30gmMeJ0geFc31syIg==Lq1jiRpNh69NYT9w";
    private static final String CALORIES_API_URL = "https://api.api-ninjas.com/v1/caloriesburned";
    private static final String NEWS_API_KEY = "a9b962f60e1640e09d955843e26924f2";
    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventService = new EventService();
        participantService = new EventParticipantService();
        venueService = new VenueService();
        loadEvents();
        showMainDashboard();
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

    private void loadEvents() {
        if (eventListView != null) {
            eventListView.setItems(FXCollections.observableArrayList(eventService.getAllEvents()));
        }
        if (currentUserId > 0) {
            loadMyEvents();
        }
    }

    private void loadMyEvents() {
        if (myEventsListView != null) {
            ApiResponse<List<Evenement>> response = eventService.getMyEvents(currentUserId);
            if (response.isSuccess()) {
                myEventsListView.setItems(FXCollections.observableArrayList(response.getData()));
            } else {
                showAlert("Error", "Failed to load your events: " + response.getMessage(), Alert.AlertType.ERROR);
            }
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
            loadMyEvents();
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
        loadMyEvents();
    }

    private class EventListCell extends ListCell<Evenement> {
        private final VBox content;
        private final Label nameLabel;
        private final Label dateLabel;
        private final Label typeLabel;
        private final Button joinButton;
        
        public EventListCell() {
            nameLabel = new Label();
            dateLabel = new Label();
            typeLabel = new Label();
            joinButton = new Button("Join");
            
            content = new VBox(5);
            content.getChildren().addAll(nameLabel, dateLabel, typeLabel);
            
            joinButton.setOnAction(e -> handleJoinEvent());
        }
        
        @Override
        protected void updateItem(Evenement event, boolean empty) {
            super.updateItem(event, empty);
            if (empty || event == null) {
                setGraphic(null);
            } else {
                nameLabel.setText(event.getNom());
                dateLabel.setText(event.getDateDebut().toString());
                typeLabel.setText(event.getType());
                
                // Style past events differently
                if (event.getDateDebut().isBefore(LocalDateTime.now())) {
                    nameLabel.setTextFill(Color.RED);
                    joinButton.setDisable(true);
                }
                
                setGraphic(content);
            }
        }
    }

    private void showMainDashboard() {
        contentArea.getChildren().clear();
        
        // Create main container with dark theme
        VBox mainContainer = new VBox(30);
        mainContainer.getStyleClass().add("dashboard-container");
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        
        // Add header
        Text headerText = new Text("Welcome to Event Management");
        headerText.getStyleClass().addAll("header-title", "fade-in");
        
        // Create stats container
        HBox statsContainer = new HBox(40);
        statsContainer.getStyleClass().addAll("stats-container", "fade-in");
        statsContainer.setAlignment(Pos.CENTER);
        
        // Get counts
        List<Evenement> events = eventService.getAllEvents();
        List<Venue> venues = venueService.getAllVenues();
        
        // Create cards with animations
        VBox eventCard = createDashboardCard(
            "Total Events",
            String.valueOf(events.size()),
            "View All Events",
            e -> showEventsList(events)
        );
        
        VBox venueCard = createDashboardCard(
            "Total Venues",
            String.valueOf(venues.size()),
            "View All Venues",
            e -> showVenuesList(venues)
        );
        
        // Add cards to stats container
        statsContainer.getChildren().addAll(eventCard, venueCard);
        
        // Add all components to main container
        mainContainer.getChildren().addAll(headerText, statsContainer);
        
        // Add fade-in animation to main container
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(mainContainer);
    }

    private VBox createDashboardCard(String title, String count, String buttonText, EventHandler<ActionEvent> action) {
        VBox card = new VBox(20);
        card.getStyleClass().addAll("card", "slide-up");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(320);
        card.setPrefHeight(220);
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setEffect(new DropShadow(20, Color.valueOf("#5271ff")));
            TranslateTransition tt = new TranslateTransition(Duration.millis(200), card);
            tt.setByY(-5);
            tt.play();
        });
        
        card.setOnMouseExited(e -> {
            card.setEffect(new DropShadow(10, Color.valueOf("#2d3545")));
            TranslateTransition tt = new TranslateTransition(Duration.millis(200), card);
            tt.setByY(5);
            tt.play();
        });
        
        Text titleText = new Text(title);
        titleText.getStyleClass().add("card-title");
        
        Text countText = new Text(count);
        countText.getStyleClass().add("card-count");
        
        Button actionButton = new Button(buttonText);
        actionButton.getStyleClass().add("view-all-button");
        actionButton.setOnAction(action);
        
        card.getChildren().addAll(titleText, countText, actionButton);
        return card;
    }

    private void showEventsList(List<Evenement> events) {
        VBox container = new VBox(20);
        container.getStyleClass().add("list-container");
        container.setPadding(new Insets(30));
        
        // Add header
        Text headerText = new Text("All Events");
        headerText.getStyleClass().add("header-title");
        
        // Add back button
        Button backButton = new Button("Back to Dashboard");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showMainDashboard());
        
        // Create list container
        VBox listContent = new VBox(15);
        listContent.getStyleClass().add("list-content");
        
        for (Evenement event : events) {
            HBox eventItem = createEventListItem(event);
            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), eventItem);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            listContent.getChildren().add(eventItem);
        }
        
        container.getChildren().addAll(backButton, headerText, listContent);
        
        // Add slide-up animation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(500), container);
        slideUp.setFromY(50);
        slideUp.setToY(0);
        slideUp.play();
        
        contentArea.getChildren().setAll(container);
    }

    private HBox createEventListItem(Evenement event) {
        HBox item = new HBox(20);
        item.getStyleClass().add("list-cell");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(20));
        
        VBox details = new VBox(10);
        details.setAlignment(Pos.CENTER_LEFT);
        
        Text name = new Text(event.getNom());
        name.getStyleClass().add("list-item-title");
        
        Text date = new Text(event.getDateDebut() + " - " + event.getDateFin());
        date.getStyleClass().add("list-item-subtitle");
        
        details.getChildren().addAll(name, date);
        
        Button detailsButton = new Button("View Details");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showEventDetails(event));
        
        HBox.setHgrow(details, Priority.ALWAYS);
        item.getChildren().addAll(details, detailsButton);
        
        // Add hover effect
        item.setOnMouseEntered(e -> {
            item.getStyleClass().add("list-cell-hover");
        });
        
        item.setOnMouseExited(e -> {
            item.getStyleClass().remove("list-cell-hover");
        });
        
        return item;
    }

    private void showVenuesList(List<Venue> venues) {
        VBox container = new VBox(20);
        container.getStyleClass().add("list-container");
        container.setPadding(new Insets(30));
        
        // Add header
        Text headerText = new Text("All Venues");
        headerText.getStyleClass().add("header-title");
        
        // Add back button
        Button backButton = new Button("Back to Dashboard");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showMainDashboard());
        
        // Create list container
        VBox listContent = new VBox(15);
        listContent.getStyleClass().add("list-content");
        
        for (Venue venue : venues) {
            HBox venueItem = createVenueListItem(venue);
            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), venueItem);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            listContent.getChildren().add(venueItem);
        }
        
        container.getChildren().addAll(backButton, headerText, listContent);
        
        // Add slide-up animation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(500), container);
        slideUp.setFromY(50);
        slideUp.setToY(0);
        slideUp.play();
        
        contentArea.getChildren().setAll(container);
    }

    private HBox createVenueListItem(Venue venue) {
        HBox item = new HBox(20);
        item.getStyleClass().add("list-cell");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(20));
        
        VBox details = new VBox(10);
        details.setAlignment(Pos.CENTER_LEFT);
        
        Text name = new Text(venue.getType());
        name.getStyleClass().add("list-item-title");
        
        Text location = new Text(venue.getLocalisation());
        location.getStyleClass().add("list-item-subtitle");
        
        details.getChildren().addAll(name, location);
        
        Button detailsButton = new Button("View Details");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showVenueDetails(venue));
        
        HBox.setHgrow(details, Priority.ALWAYS);
        item.getChildren().addAll(details, detailsButton);
        
        // Add hover effect
        item.setOnMouseEntered(e -> {
            item.getStyleClass().add("list-cell-hover");
        });
        
        item.setOnMouseExited(e -> {
            item.getStyleClass().remove("list-cell-hover");
        });
        
        return item;
    }

    private void showVenueDetails(Venue venue) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Venue Details");
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.setHeaderText(venue.getType());
        
        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content");
        
        Text location = new Text("Location: " + venue.getLocalisation());
        Text capacity = new Text("Capacity: " + venue.getCapacite());
        
        content.getChildren().addAll(location, capacity);
        alert.getDialogPane().setContent(content);
        
        alert.showAndWait();
    }

    @FXML
    private void showDashboard() {
        showMainDashboard();
    }

    @FXML
    private void showProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserProfile.fxml"));
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            showError("Error loading profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        loadMyEvents();
        
        container.getChildren().addAll(headerText, myEventsListView);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(container);
    }

    @FXML
    private void showAvailableEvents() {
        contentArea.getChildren().clear();
        
        // Create main container with split view
        HBox mainContainer = new HBox(20);
        mainContainer.getStyleClass().add("content-container");
        mainContainer.setPadding(new Insets(20));
        
        // Left side - Events list
        VBox leftContainer = new VBox(20);
        leftContainer.setPrefWidth(400);
        
        Text headerText = new Text("Available Events");
        headerText.getStyleClass().add("section-header");
        
        // Create ListView for available events
        eventListView = new ListView<>();
        eventListView.getStyleClass().add("event-list");
        VBox.setVgrow(eventListView, Priority.ALWAYS);
        
        // Setup the list view
        setupListViews();
        
        // Load all events
        loadEvents();
        
        leftContainer.getChildren().addAll(headerText, eventListView);
        
        // Right side - Event details
        VBox rightContainer = new VBox(20);
        rightContainer.getStyleClass().add("details-container");
        rightContainer.setPrefWidth(400);
        VBox.setVgrow(rightContainer, Priority.ALWAYS);
        
        // Details header
        Text detailsHeader = new Text("Event Details");
        detailsHeader.getStyleClass().add("section-header");
        
        // Details content
        VBox detailsContent = new VBox(15);
        detailsContent.getStyleClass().add("details-content");
        detailsContent.setPadding(new Insets(20));
        
        Label nameLabel = new Label();
        nameLabel.getStyleClass().add("details-title");
        
        Label typeLabel = new Label();
        Label descLabel = new Label();
        Label dateLabel = new Label();
        Label capacityLabel = new Label();
        
        Button joinButton = new Button("Join Event");
        joinButton.getStyleClass().add("join-button");
        joinButton.setDisable(true);
        
        detailsContent.getChildren().addAll(
            nameLabel, typeLabel, descLabel, dateLabel, capacityLabel, joinButton
        );
        
        // News section
        VBox newsContainer = new VBox(15);
        newsContainer.getStyleClass().add("news-container");
        
        Text newsHeader = new Text("Related News");
        newsHeader.getStyleClass().add("section-header");
        
        ListView<String> newsList = new ListView<>();
        newsList.getStyleClass().add("news-list");
        VBox.setVgrow(newsList, Priority.ALWAYS);
        
        newsContainer.getChildren().addAll(newsHeader, newsList);
        
        rightContainer.getChildren().addAll(detailsHeader, detailsContent, newsContainer);
        
        // Update event details when selection changes
        eventListView.getSelectionModel().selectedItemProperty().addListener((obs, old, event) -> {
            if (event != null) {
                nameLabel.setText("Event: " + event.getNom());
                typeLabel.setText("Type: " + event.getType());
                descLabel.setText("Description: " + event.getDescription());
                dateLabel.setText(String.format("Date: %s - %s", 
                    event.getDateDebut().toString(), 
                    event.getDateFin().toString()));
                capacityLabel.setText("Max Participants: " + event.getCapaciteMax());
                
                joinButton.setDisable(event.getDateDebut().isBefore(LocalDateTime.now()));
                
                // Fetch related news
                fetchNewsForEvent(event, newsList);
            }
        });
        
        // Handle join button click
        joinButton.setOnAction(e -> {
            Evenement selectedEvent = eventListView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                handleJoinEvent();
            }
        });
        
        mainContainer.getChildren().addAll(leftContainer, rightContainer);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(mainContainer);
    }

    private void fetchNewsForEvent(Evenement event, ListView<String> newsList) {
        // Call news API to get related news
        String searchQuery = event.getNom().replace(" ", "+");
        String newsApiUrl = "https://newsapi.org/v2/everything" +
            "?q=" + searchQuery +
            "&apiKey=" + NEWS_API_KEY +
            "&language=en" +
            "&sortBy=relevancy" +
            "&pageSize=10";
            
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(newsApiUrl))
            .header("User-Agent", "Java/17")  // Add User-Agent header
            .build();
            
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    
                    if (jsonResponse.getString("status").equals("error")) {
                        Platform.runLater(() -> 
                            showError("API Error: " + jsonResponse.getString("message"))
                        );
                        return;
                    }
                    
                    JSONArray articles = jsonResponse.getJSONArray("articles");
                    List<String> newsItems = new ArrayList<>();
                    
                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject article = articles.getJSONObject(i);
                        String title = article.getString("title");
                        String description = article.optString("description", "No description available");
                        String source = article.getJSONObject("source").optString("name", "Unknown Source");
                        String url = article.getString("url");
                        String date = article.getString("publishedAt").split("T")[0];
                        
                        String newsItem = "📰 " + title + "\n\n" +
                            "🔍 " + description + "\n\n" +
                            "📅 Published: " + date + "\n" +
                            "📍 Source: " + source + "\n" +
                            "🔗 " + url + "\n\n" +
                            "----------------------------------------";
                        
                        newsItems.add(newsItem);
                    }
                    
                    if (newsItems.isEmpty()) {
                        newsItems.add("No news found related to: " + event.getNom());
                    }
                    
                    Platform.runLater(() -> {
                        newsList.setItems(FXCollections.observableArrayList(newsItems));
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> 
                        showError("Error parsing news data: " + e.getMessage())
                    );
                }
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> 
                    showError("Error fetching news: " + throwable.getMessage())
                );
                return null;
            });
    }

    @FXML
    private void showCaloriesBurned() {
        contentArea.getChildren().clear();
        
        VBox container = new VBox(20);
        container.getStyleClass().add("content-container");
        container.setPadding(new Insets(20));
        
        Text headerText = new Text("Calories Burned Calculator");
        headerText.getStyleClass().add("section-header");
        
        // Create input fields
        ComboBox<String> activityCombo = new ComboBox<>();
        activityCombo.getItems().addAll(
            "running", "walking", "swimming", "cycling", 
            "basketball", "football", "tennis", "yoga"
        );
        activityCombo.setPromptText("Select Activity");
        
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        
        Button calculateButton = new Button("Calculate");
        Label resultLabel = new Label();
        resultLabel.getStyleClass().add("result-text");
        
        calculateButton.setOnAction(e -> {
            try {
                String activity = activityCombo.getValue();
                int duration = Integer.parseInt(durationField.getText());
                
                if (activity == null || activity.isEmpty()) {
                    showError("Please select an activity");
                    return;
                }
                
                // Call calories API
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CALORIES_API_URL + "?activity=" + activity))
                    .header("X-Api-Key", CALORIES_API_KEY)
                    .build();
                
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject result = jsonArray.getJSONObject(0);
                            double caloriesPerHour = result.getDouble("calories_per_hour");
                            double totalCalories = (caloriesPerHour / 60) * duration;
                            
                            javafx.application.Platform.runLater(() -> {
                                resultLabel.setText(String.format(
                                    "Calories burned: %.2f\nActivity: %s\nDuration: %d minutes",
                                    totalCalories, activity, duration
                                ));
                            });
                        }
                    })
                    .exceptionally(throwable -> {
                        javafx.application.Platform.runLater(() -> 
                            showError("Error calculating calories: " + throwable.getMessage())
                        );
                        return null;
                    });
                    
            } catch (NumberFormatException ex) {
                showError("Please enter a valid duration");
            }
        });
        
        container.getChildren().addAll(
            headerText,
            activityCombo,
            durationField,
            calculateButton,
            resultLabel
        );
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(container);
    }

    @FXML
    private void showSportsNews() {
        contentArea.getChildren().clear();
        
        VBox container = new VBox(20);
        container.getStyleClass().add("content-container");
        container.setPadding(new Insets(20));
        
        Text headerText = new Text("Sports News");
        headerText.getStyleClass().add("section-header");
        
        // Create search controls
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search sports news...");
        searchField.setPrefWidth(300);
        
        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("search-button");
        
        searchBox.getChildren().addAll(searchField, searchButton);
        
        // Create news list
        ListView<String> newsList = new ListView<>();
        newsList.getStyleClass().add("news-list");
        VBox.setVgrow(newsList, Priority.ALWAYS);
        
        // Function to load news
        Runnable loadNews = () -> {
            String searchQuery = searchField.getText().isEmpty() ? "sports" : searchField.getText() + " sports";
            String newsApiUrl = NEWS_API_URL +
                "?q=" + searchQuery +
                "&apiKey=" + NEWS_API_KEY +
                "&language=en" +
                "&sortBy=publishedAt" +
                "&pageSize=20";
                
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(newsApiUrl))
                .build();
                
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray articles = jsonResponse.getJSONArray("articles");
                    
                    List<String> newsItems = new ArrayList<>();
                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject article = articles.getJSONObject(i);
                        String title = article.getString("title");
                        String description = article.getString("description");
                        String url = article.getString("url");
                        String date = article.getString("publishedAt").split("T")[0];
                        
                        newsItems.add(String.format("%s\n%s\nPublished: %s\n%s\n", 
                            title, description, date, url));
                    }
                    
                    Platform.runLater(() -> {
                        newsList.setItems(FXCollections.observableArrayList(newsItems));
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> 
                        showError("Error fetching news: " + throwable.getMessage())
                    );
                    return null;
                });
        };
        
        // Set up search button action
        searchButton.setOnAction(e -> loadNews.run());
        
        // Set up enter key press in search field
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loadNews.run();
            }
        });
        
        container.getChildren().addAll(headerText, searchBox, newsList);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        contentArea.getChildren().add(container);
        
        // Load initial news
        loadNews.run();
    }
} 