package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import tn.esprit.pidev.models.Venue;
import tn.esprit.pidev.services.VenueService;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class VenueManagementController implements Initializable {

    @FXML private TableView<Venue> venueTable;
    @FXML private TableColumn<Venue, Integer> idColumn;
    @FXML private TableColumn<Venue, String> typeColumn;
    @FXML private TableColumn<Venue, String> locationColumn;
    @FXML private TableColumn<Venue, Integer> capacityColumn;
    @FXML private TableColumn<Venue, String> statusColumn;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label statusLabel;
    @FXML private Label totalVenuesLabel;
    @FXML private Label availableVenuesLabel;

    private VenueService venueService;
    private ObservableList<Venue> venueList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        venueService = new VenueService();
        setupTableColumns();
        setupStatusFilter();
        loadVenues();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("courtID"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void setupStatusFilter() {
        statusFilter.getItems().addAll("All", "Disponible", "Réservé", "Maintenance");
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> filterVenues());
    }

    private void loadVenues() {
        venueList = FXCollections.observableArrayList(venueService.getAllVenues());
        venueTable.setItems(venueList);
        updateStatusBar();
    }

    private void filterVenues() {
        String status = statusFilter.getValue();
        if (status.equals("All")) {
            loadVenues();
        } else {
            venueList = FXCollections.observableArrayList(
                venueService.getAllVenues().stream()
                    .filter(v -> v.getStatut().equals(status))
                    .toList()
            );
            venueTable.setItems(venueList);
            updateStatusBar();
        }
    }

    @FXML
    private void showAddVenueDialog() {
        Dialog<Venue> dialog = new Dialog<>();
        dialog.setTitle("Add New Venue");
        dialog.setHeaderText("Enter venue details");

        GridPane grid = createVenueDialogContent();
        dialog.getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                return getVenueFromDialog(grid);
            }
            return null;
        });

        Optional<Venue> result = dialog.showAndWait();
        result.ifPresent(venue -> {
            if (venueService.addVenue(venue)) {
                loadVenues();
                statusLabel.setText("Venue added successfully");
            } else {
                showError("Failed to add venue");
            }
        });
    }

    @FXML
    private void showEditVenueDialog() {
        Venue selectedVenue = venueTable.getSelectionModel().getSelectedItem();
        if (selectedVenue == null) {
            showError("Please select a venue to edit");
            return;
        }

        Dialog<Venue> dialog = new Dialog<>();
        dialog.setTitle("Edit Venue");
        dialog.setHeaderText("Edit venue details");

        GridPane grid = createVenueDialogContent();
        populateDialogWithVenue(grid, selectedVenue);
        dialog.getDialogPane().setContent(grid);

        ButtonType editButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == editButton) {
                Venue updatedVenue = getVenueFromDialog(grid);
                updatedVenue.setCourtID(selectedVenue.getCourtID());
                return updatedVenue;
            }
            return null;
        });

        Optional<Venue> result = dialog.showAndWait();
        result.ifPresent(venue -> {
            if (venueService.updateVenue(venue)) {
                loadVenues();
                statusLabel.setText("Venue updated successfully");
            } else {
                showError("Failed to update venue");
            }
        });
    }

    @FXML
    private void deleteVenue() {
        Venue selectedVenue = venueTable.getSelectionModel().getSelectedItem();
        if (selectedVenue == null) {
            showError("Please select a venue to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Venue");
        alert.setHeaderText("Delete Venue");
        alert.setContentText("Are you sure you want to delete this venue?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (venueService.deleteVenue(selectedVenue.getCourtID())) {
                loadVenues();
                statusLabel.setText("Venue deleted successfully");
            } else {
                showError("Failed to delete venue");
            }
        }
    }

    @FXML
    private void showMaintenanceDialog() {
        Venue selectedVenue = venueTable.getSelectionModel().getSelectedItem();
        if (selectedVenue == null) {
            showError("Please select a venue to schedule maintenance");
            return;
        }

        Dialog<Date[]> dialog = new Dialog<>();
        dialog.setTitle("Schedule Maintenance");
        dialog.setHeaderText("Schedule maintenance for " + selectedVenue.getType());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        grid.add(new Label("Start Date:"), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(new Label("End Date:"), 0, 1);
        grid.add(endDatePicker, 1, 1);

        ButtonType scheduleButton = new ButtonType("Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(scheduleButton, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == scheduleButton) {
                return new Date[] {
                    Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                };
            }
            return null;
        });

        Optional<Date[]> result = dialog.showAndWait();
        result.ifPresent(dates -> {
            if (venueService.scheduleVenueMaintenance(selectedVenue.getCourtID(), dates[0], dates[1])) {
                loadVenues();
                statusLabel.setText("Maintenance scheduled successfully");
            } else {
                showError("Failed to schedule maintenance");
            }
        });
    }

    private GridPane createVenueDialogContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField typeField = new TextField();
        typeField.setPromptText("Venue type");
        TextField locationField = new TextField();
        locationField.setPromptText("Location");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Disponible", "Réservé", "Maintenance");
        statusCombo.setValue("Disponible");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationField, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);

        grid.getProperties().put("typeField", typeField);
        grid.getProperties().put("locationField", locationField);
        grid.getProperties().put("capacityField", capacityField);
        grid.getProperties().put("statusCombo", statusCombo);

        return grid;
    }

    private Venue getVenueFromDialog(GridPane grid) {
        TextField typeField = (TextField) grid.getProperties().get("typeField");
        TextField locationField = (TextField) grid.getProperties().get("locationField");
        TextField capacityField = (TextField) grid.getProperties().get("capacityField");
        ComboBox<String> statusCombo = (ComboBox<String>) grid.getProperties().get("statusCombo");

        Venue venue = new Venue();
        venue.setType(typeField.getText());
        venue.setLocalisation(locationField.getText());
        venue.setCapacite(Integer.parseInt(capacityField.getText()));
        venue.setStatut(statusCombo.getValue());

        return venue;
    }

    private void populateDialogWithVenue(GridPane grid, Venue venue) {
        TextField typeField = (TextField) grid.getProperties().get("typeField");
        TextField locationField = (TextField) grid.getProperties().get("locationField");
        TextField capacityField = (TextField) grid.getProperties().get("capacityField");
        ComboBox<String> statusCombo = (ComboBox<String>) grid.getProperties().get("statusCombo");

        typeField.setText(venue.getType());
        locationField.setText(venue.getLocalisation());
        capacityField.setText(String.valueOf(venue.getCapacite()));
        statusCombo.setValue(venue.getStatut());
    }

    private void updateStatusBar() {
        totalVenuesLabel.setText("Total Venues: " + venueList.size());
        long availableCount = venueList.stream()
            .filter(v -> v.getStatut().equals("Disponible"))
            .count();
        availableVenuesLabel.setText("Available: " + availableCount);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 