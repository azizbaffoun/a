package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.pidev.models.Event;
import tn.esprit.pidev.services.EventService;
import tn.esprit.pidev.services.EventParticipantService;

import java.net.URL;
import java.util.ResourceBundle;

public class MyEventsController implements Initializable {

    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, Integer> idColumn;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> typeColumn;
    @FXML private TableColumn<Event, String> statusColumn;

    private EventService eventService;
    private EventParticipantService participantService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventService = new EventService();
        participantService = new EventParticipantService();
        
        setupTableColumns();
        loadMyEvents();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void loadMyEvents() {
        // TODO: Implement loading user's events
        // This will need the current user's ID
    }

    @FXML
    private void viewEventDetails() {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showError("Please select an event to view");
            return;
        }
        // TODO: Implement event details view
    }

    @FXML
    private void leaveEvent() {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showError("Please select an event to leave");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Event");
        alert.setHeaderText("Leave Event");
        alert.setContentText("Are you sure you want to leave this event?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement leaving event
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