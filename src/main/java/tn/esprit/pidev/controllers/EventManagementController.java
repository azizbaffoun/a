package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.Venue;
import tn.esprit.pidev.services.EventService;
import tn.esprit.pidev.services.VenueService;
import javafx.util.Callback;
import javafx.scene.Node;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EventManagementController implements Initializable {

    @FXML private TableView<Evenement> eventTable;
    @FXML private TableColumn<Evenement, Integer> idColumn;
    @FXML private TableColumn<Evenement, String> nameColumn;
    @FXML private TableColumn<Evenement, String> detailsColumn;
    @FXML private TableColumn<Evenement, LocalDateTime> startDateColumn;
    @FXML private TableColumn<Evenement, LocalDateTime> endDateColumn;
    @FXML private TableColumn<Evenement, String> typeColumn;
    @FXML private TableColumn<Evenement, Integer> maxParticipantsColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private Label totalEventsLabel;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField typeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField maxParticipantsField;
    @FXML private TextField venueTypeField;
    @FXML private TextField venueLocationField;
    @FXML private TextField venueCapacityField;
    @FXML private ListView<Venue> venueListView;

    private EventService eventService;
    private VenueService venueService;
    private ObservableList<Evenement> eventList;
    private Evenement selectedEvent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            eventService = new EventService();
            venueService = new VenueService();
            setupTableColumns();
            loadEvents();
            loadVenues();
            
            // Add listener for selection changes
            eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedEvent = newSelection;
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            });
            
            // Add search functionality
            searchField.textProperty().addListener((obs, oldText, newText) -> {
                if (newText != null && !newText.isEmpty()) {
                    eventTable.getItems().setAll(eventService.searchEvents(newText));
                } else {
                    loadEvents();
                }
            });
            
            statusLabel.setText("Application initialized successfully");
        } catch (Exception e) {
            showError("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        maxParticipantsColumn.setCellValueFactory(new PropertyValueFactory<>("capaciteMax"));

        // Format date columns for LocalDateTime
        startDateColumn.setCellFactory(column -> new TableCell<Evenement, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });
        endDateColumn.setCellFactory(column -> new TableCell<Evenement, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });
    }

    private void loadEvents() {
        eventList = FXCollections.observableArrayList(eventService.getAllEvents());
        eventTable.setItems(eventList);
        updateStatusBar();
    }

    private void loadVenues() {
        try {
            List<Venue> venues = venueService.getAllVenues();
            venueListView.setItems(FXCollections.observableArrayList(venues));
            venueListView.setCellFactory(new Callback<ListView<Venue>, ListCell<Venue>>() {
                @Override
                public ListCell<Venue> call(ListView<Venue> param) {
                    return new ListCell<Venue>() {
                        @Override
                        protected void updateItem(Venue venue, boolean empty) {
                            super.updateItem(venue, empty);
                            if (empty || venue == null) {
                                setText(null);
                            } else {
                                setText(String.format("%s - %s (Capacity: %d)", 
                                    venue.getType(), 
                                    venue.getLocalisation(), 
                                    venue.getCapacite()));
                            }
                        }
                    };
                }
            });
            statusLabel.setText("Venues loaded successfully");
        } catch (Exception e) {
            statusLabel.setText("Error loading venues: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadEvents();
        } else {
            eventList = FXCollections.observableArrayList(eventService.searchEvents(searchText));
            eventTable.setItems(eventList);
            updateStatusBar();
        }
    }

    @FXML
    private void showAddEventDialog() {
        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle("Add New Event");
        dialog.setHeaderText("Enter event details");

        // Create dialog content
        GridPane grid = createEventDialogContent();
        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                return getEventFromDialog(grid);
            }
            return null;
        });

        Optional<Evenement> result = dialog.showAndWait();
        result.ifPresent(event -> {
            if (eventService.addEvent(event)) {
                loadEvents();
                statusLabel.setText("Event added successfully");
            } else {
                showError("Failed to add event");
            }
        });
    }

    @FXML
    private void showEditEventDialog() {
        if (selectedEvent == null) {
            showError("Please select an event to edit");
            return;
        }

        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle("Edit Event");
        dialog.setHeaderText("Edit event details");

        // Create dialog content
        GridPane grid = createEventDialogContent();
        populateDialogWithEvent(grid, selectedEvent);
        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType editButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(buttonType -> {
            if (buttonType == editButton) {
                Evenement updatedEvent = getEventFromDialog(grid);
                updatedEvent.setId(selectedEvent.getId());
                return updatedEvent;
            }
            return null;
        });

        Optional<Evenement> result = dialog.showAndWait();
        result.ifPresent(event -> {
            if (eventService.updateEvent(event)) {
                loadEvents();
                statusLabel.setText("Event updated successfully");
            } else {
                showError("Failed to update event");
            }
        });
    }

    @FXML
    private void deleteEvent() {
        if (selectedEvent == null) {
            showError("Please select an event to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("Delete Event");
        alert.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (eventService.deleteEvent(selectedEvent.getId().intValue())) {
                loadEvents();
                statusLabel.setText("Event deleted successfully");
            } else {
                showError("Failed to delete event");
            }
        }
    }

    @FXML
    private void handleAddVenue() {
        try {
            // Validate input fields
            if (venueTypeField.getText().isEmpty() || 
                venueLocationField.getText().isEmpty() || 
                venueCapacityField.getText().isEmpty()) {
                statusLabel.setText("Please fill all venue fields");
                return;
            }

            // Parse capacity
            int capacity;
            try {
                capacity = Integer.parseInt(venueCapacityField.getText());
                if (capacity <= 0) {
                    statusLabel.setText("Capacity must be a positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Invalid capacity value");
                return;
            }

            // Create new venue
            Venue venue = new Venue();
            venue.setType(venueTypeField.getText());
            venue.setLocalisation(venueLocationField.getText());
            venue.setCapacite(capacity);
            venue.setStatut("Available");

            // Add venue
            if (venueService.addVenue(venue)) {
                loadVenues();
                clearVenueFields();
                statusLabel.setText("Venue added successfully");
            } else {
                statusLabel.setText("Failed to add venue");
            }
        } catch (Exception e) {
            statusLabel.setText("Error adding venue: " + e.getMessage());
        }
    }

    private GridPane createEventDialogContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Event Name");
        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Event Details");
        TextField typeField = new TextField();
        typeField.setPromptText("Event Type");
        TextField maxParticipantsField = new TextField();
        maxParticipantsField.setPromptText("Max Participants");
        
        // Create DatePickers with date validation
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        
        // Set minimum date to today for both date pickers
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        
        // End date can't be before start date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        
        // Add listeners for validation
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && endDatePicker.getValue() != null && 
                endDatePicker.getValue().isBefore(newVal)) {
                endDatePicker.setValue(newVal);
            }
        });
        
        // Only allow numbers in max participants field
        maxParticipantsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                maxParticipantsField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Details:*"), 0, 1);
        grid.add(detailsArea, 1, 1);
        grid.add(new Label("Type:*"), 0, 2);
        grid.add(typeField, 1, 2);
        grid.add(new Label("Start Date:*"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:*"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(new Label("Max Participants:*"), 0, 5);
        grid.add(maxParticipantsField, 1, 5);

        return grid;
    }

    private Evenement getEventFromDialog(GridPane grid) {
        TextField nameField = (TextField) getNodeFromGridPane(grid, 1, 0);
        TextArea detailsArea = (TextArea) getNodeFromGridPane(grid, 1, 1);
        TextField typeField = (TextField) getNodeFromGridPane(grid, 1, 2);
        DatePicker startDatePicker = (DatePicker) getNodeFromGridPane(grid, 1, 3);
        DatePicker endDatePicker = (DatePicker) getNodeFromGridPane(grid, 1, 4);
        TextField maxParticipantsField = (TextField) getNodeFromGridPane(grid, 1, 5);

        // Validate required fields
        StringBuilder errors = new StringBuilder();
        if (nameField.getText().trim().isEmpty()) errors.append("Name is required\n");
        if (detailsArea.getText().trim().isEmpty()) errors.append("Details are required\n");
        if (typeField.getText().trim().isEmpty()) errors.append("Type is required\n");
        if (startDatePicker.getValue() == null) errors.append("Start date is required\n");
        if (endDatePicker.getValue() == null) errors.append("End date is required\n");
        if (maxParticipantsField.getText().trim().isEmpty()) errors.append("Max participants is required\n");

        if (errors.length() > 0) {
            showError(errors.toString());
            return null;
        }

        // Validate dates
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showError("End date must be after start date");
            return null;
        }

        // Create event if validation passes
        Evenement event = new Evenement();
        event.setNom(nameField.getText().trim());
        event.setDescription(detailsArea.getText().trim());
        event.setType(typeField.getText().trim());
        event.setDateDebut(startDatePicker.getValue().atStartOfDay());
        event.setDateFin(endDatePicker.getValue().atStartOfDay());
        event.setCapaciteMax(Integer.parseInt(maxParticipantsField.getText().trim()));
        
        return event;
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void populateDialogWithEvent(GridPane grid, Evenement event) {
        TextField nameField = (TextField) getNodeFromGridPane(grid, 1, 0);
        TextArea detailsArea = (TextArea) getNodeFromGridPane(grid, 1, 1);
        TextField typeField = (TextField) getNodeFromGridPane(grid, 1, 2);
        DatePicker startDatePicker = (DatePicker) getNodeFromGridPane(grid, 1, 3);
        DatePicker endDatePicker = (DatePicker) getNodeFromGridPane(grid, 1, 4);
        TextField maxParticipantsField = (TextField) getNodeFromGridPane(grid, 1, 5);

        nameField.setText(event.getNom());
        detailsArea.setText(event.getDescription());
        typeField.setText(event.getType());
        startDatePicker.setValue(event.getDateDebut().toLocalDate());
        endDatePicker.setValue(event.getDateFin().toLocalDate());
        maxParticipantsField.setText(String.valueOf(event.getCapaciteMax()));
    }

    private void populateFields(Evenement event) {
        nameField.setText(event.getNom());
        descriptionArea.setText(event.getDescription());
        typeField.setText(event.getType());
        startDatePicker.setValue(event.getDateDebut().toLocalDate());
        endDatePicker.setValue(event.getDateFin().toLocalDate());
        maxParticipantsField.setText(String.valueOf(event.getCapaciteMax()));
    }

    private void updateStatusBar() {
        totalEventsLabel.setText("Total Events: " + eventList.size());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearVenueFields() {
        venueTypeField.clear();
        venueLocationField.clear();
        venueCapacityField.clear();
    }
} 