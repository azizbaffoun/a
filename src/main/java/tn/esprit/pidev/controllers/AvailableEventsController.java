package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.services.EventService;
import tn.esprit.pidev.services.EventParticipantService;

import java.net.URL;
import java.util.ResourceBundle;

public class AvailableEventsController implements Initializable {

    @FXML private TableView<Evenement> eventsTable;
    @FXML private TableColumn<Evenement, Integer> idColumn;
    @FXML private TableColumn<Evenement, String> nameColumn;
    @FXML private TableColumn<Evenement, String> dateColumn;
    @FXML private TableColumn<Evenement, String> typeColumn;
    @FXML private TableColumn<Evenement, Integer> capacityColumn;
    @FXML private TableColumn<Evenement, String> venueColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;

    private EventService eventService;
    private EventParticipantService participantService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventService = new EventService();
        participantService = new EventParticipantService();
        
        setupTableColumns();
        setupTypeFilter();
        loadAvailableEvents();

        // Add search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents();
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capaciteMax"));
    }

    private void setupTypeFilter() {
        typeFilter.getItems().addAll("All", "Tournament", "Training", "Competition");
        typeFilter.setValue("All");
        typeFilter.setOnAction(e -> filterEvents());
    }

    private void loadAvailableEvents() {
        // TODO: Implement loading available events
        // This should exclude events the user is already part of
    }

    private void filterEvents() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = typeFilter.getValue();
        
        // TODO: Implement event filtering based on search text and type
    }

    @FXML
    private void viewEventDetails() {
        Evenement selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showError("Please select an event to view");
            return;
        }
        // TODO: Implement event details view
    }

    @FXML
    private void joinEvent() {
        Evenement selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showError("Please select an event to join");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Join Event");
        alert.setHeaderText("Join Event");
        alert.setContentText("Are you sure you want to join this event?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement joining event
                // This will need the current user's ID
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
} 