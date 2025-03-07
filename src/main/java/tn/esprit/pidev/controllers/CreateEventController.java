package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;

import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.services.EventService;

import java.time.LocalDateTime;

public class CreateEventController {
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField typeField;
    @FXML private TextField capacityField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    private EventService eventService;
    private int userId;
    
    @FXML
    public void initialize() {
        eventService = new EventService();
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @FXML
    private void handleCreateEvent() {
        try {
            // Validate inputs
            if (nameField.getText().isEmpty() || descriptionArea.getText().isEmpty() || 
                typeField.getText().isEmpty() || capacityField.getText().isEmpty() ||
                startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showAlert("Error", "All fields are required", AlertType.ERROR);
                return;
            }
            
            // Create event object
            Evenement event = new Evenement();
            event.setNom(nameField.getText());
            event.setDescription(descriptionArea.getText());
            event.setType(typeField.getText());
            event.setCapaciteMax(Integer.parseInt(capacityField.getText()));
            event.setDateDebut(LocalDateTime.of(startDatePicker.getValue(), java.time.LocalTime.now()));
            event.setDateFin(LocalDateTime.of(endDatePicker.getValue(), java.time.LocalTime.now()));
            event.setOrganisateurId(userId);
            
            // Save event
            var response = eventService.createEvent(event);
            if (response.isSuccess()) {
                showAlert("Success", "Event created successfully!", AlertType.INFORMATION);
                closeWindow();
            } else {
                showAlert("Error", response.getMessage(), AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Capacity must be a valid number", AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Error creating event: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 