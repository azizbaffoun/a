package tn.esprit.jappa.controllers;

import tn.esprit.jappa.models.TerrainSiege;
import tn.esprit.jappa.services.TerrainSiegeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class TerrainSeatingController {
    @FXML private GridPane seatingGrid;
    @FXML private Label totalLabel;
    @FXML private Button confirmButton;
    @FXML private Label terrainName;
    @FXML private Label dateTime;
    @FXML private Label selectedSeatsLabel;
    
    private TerrainSiegeService siegeService;
    private ObservableList<TerrainSiege> selectedSeats = FXCollections.observableArrayList();
    private int terrainId;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private double totalPrice = 0.0;

    public void initialize(TerrainSiegeService siegeService, int terrainId, 
                         LocalDate date, LocalTime time, String tName) {
        this.siegeService = siegeService;
        this.terrainId = terrainId;
        this.reservationDate = date;
        this.reservationTime = time;
        this.terrainName.setText(tName);
        this.dateTime.setText(String.format("%s à %s", date, time));
        
        loadSeats();
        setupListeners();
    }

    private void loadSeats() {
        try {
            List<TerrainSiege> availableSeats = siegeService.getAvailableSeats(terrainId, reservationDate, reservationTime);
            populateSeatingGrid(availableSeats);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des sièges", e.getMessage());
        }
    }

    private void populateSeatingGrid(List<TerrainSiege> seats) {
        seatingGrid.getChildren().clear();
        
        // Find max row and column
        int maxRow = seats.stream()
            .map(s -> s.getRangee().charAt(0)) // Get first character of row (e.g., 'A')
            .mapToInt(c -> c - 'A' + 1) // Convert to 1-based index
            .max()
            .orElse(0);
            
        int maxCol = seats.stream()
            .mapToInt(s -> Integer.parseInt(s.getNumero()))
            .max()
            .orElse(0);

        // Add column headers (numbers)
        for (int col = 1; col <= maxCol; col++) {
            Label colLabel = new Label(String.valueOf(col));
            colLabel.getStyleClass().add("seat-header");
            seatingGrid.add(colLabel, col, 0);
        }

        // Add row headers (letters) and seats
        for (int rowIndex = 1; rowIndex <= maxRow; rowIndex++) {
            // Convert row number back to letter (A=1, B=2, etc.)
            char rowLetter = (char)('A' + rowIndex - 1);
            Label rowLabel = new Label(String.valueOf(rowLetter));
            rowLabel.getStyleClass().add("seat-header");
            seatingGrid.add(rowLabel, 0, rowIndex);

            final String currentRow = String.valueOf(rowLetter);
            final int currentRowNum = rowIndex;
            seats.stream()
                .filter(s -> s.getRangee().equals(currentRow))
                .forEach(siege -> {
                    ToggleButton seatButton = createSeatButton(siege);
                    seatingGrid.add(seatButton, Integer.parseInt(siege.getNumero()), currentRowNum);
                });
        }
    }

    private ToggleButton createSeatButton(TerrainSiege siege) {
        ToggleButton button = new ToggleButton();
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.getStyleClass().add("seat-button");
        
        if ("Réservé".equals(siege.getStatut())) {
            button.getStyleClass().add("seat-reserved");
            button.setDisable(true);
        } else {
            button.getStyleClass().add("seat-available");
        }

        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                siege.setSelected(true);
                selectedSeats.add(siege);
                button.getStyleClass().add("seat-selected");
            } else {
                siege.setSelected(false);
                selectedSeats.remove(siege);
                button.getStyleClass().remove("seat-selected");
            }
            updateTotalAndSelection();
        });

        // Tooltip showing seat info
        Tooltip tooltip = new Tooltip(String.format("Siège %s-%s\nPrix: %.2f€", 
            siege.getRangee(), siege.getNumero(), siege.getPrix()));
        button.setTooltip(tooltip);

        return button;
    }

    private void setupListeners() {
        confirmButton.setOnAction(e -> handleConfirmation());
        confirmButton.setDisable(true);
    }

    private void updateTotalAndSelection() {
        totalPrice = selectedSeats.stream()
            .mapToDouble(TerrainSiege::getPrix)
            .sum();
        
        String selectedSeatsStr = selectedSeats.stream()
            .map(s -> String.format("%s-%s", s.getRangee(), s.getNumero()))
            .collect(Collectors.joining(", "));
        
        selectedSeatsLabel.setText("Sièges sélectionnés: " + selectedSeatsStr);
        totalLabel.setText(String.format("Total: %.2f€", totalPrice));
        confirmButton.setDisable(selectedSeats.isEmpty());
    }

    @FXML
    private void handleConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la réservation");
        alert.setHeaderText("Voulez-vous confirmer la réservation ?");
        alert.setContentText(String.format("Total à payer: %.2f€", totalPrice));
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Call service to save reservation
                showSuccess("Réservation confirmée", 
                    "Votre réservation a été enregistrée avec succès.");
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 