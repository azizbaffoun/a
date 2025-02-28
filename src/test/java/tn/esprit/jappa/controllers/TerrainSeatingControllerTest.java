package tn.esprit.jappa.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import tn.esprit.jappa.services.TerrainSiegeService;
import tn.esprit.jappa.utils.TestDatabaseInitializer;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class TerrainSeatingControllerTest {
    private TerrainSeatingController controller;
    private Connection connection;

    @Start
    public void start(Stage stage) throws Exception {
        // Initialize test database
        connection = TestDatabaseInitializer.initializeTestDatabase();
        
        // Setup test data
        try (Statement stmt = connection.createStatement()) {
            // Insert test terrain
            stmt.execute("INSERT INTO terrain (courtID, type, localisation, capacite, statut) VALUES (1, 'Test Terrain', 'Test Location', 100, 'Disponible')");
            
            // Insert test seats
            stmt.execute("INSERT INTO terrain_siege (siegeID, terrainID, rangee, numero, statut, prix) VALUES " +
                        "(1, 1, 'A', '1', 'Disponible', 100.0), " +
                        "(2, 1, 'A', '2', 'Disponible', 100.0)");
        }

        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/terrain_seating.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
        
        controller = loader.getController();
        TerrainSiegeService siegeService = new TerrainSiegeService(connection);
        controller.initialize(siegeService, 1, LocalDate.now(), LocalTime.now(), "Test Terrain");
    }

    @Test
    public void shouldDisplayTerrainName(FxRobot robot) {
        Label nameLabel = robot.lookup("#terrainName").queryAs(Label.class);
        assertEquals("Test Terrain", nameLabel.getText());
    }

    @Test
    public void shouldDisableConfirmButtonInitially(FxRobot robot) {
        Button confirmButton = robot.lookup("#confirmButton").queryAs(Button.class);
        assertTrue(confirmButton.isDisabled());
    }

    @Test
    public void shouldUpdateTotalWhenSelectingSeat(FxRobot robot) {
        // Click first seat
        robot.clickOn(".seat-available");
        
        Label totalLabel = robot.lookup("#totalLabel").queryAs(Label.class);
        assertEquals("Total: 100.0€", totalLabel.getText());
    }

    @Test
    public void shouldUpdateSelectedSeatsLabel(FxRobot robot) {
        robot.clickOn(".seat-available");
        
        Label selectedSeatsLabel = robot.lookup("#selectedSeatsLabel").queryAs(Label.class);
        assertEquals("Sièges sélectionnés: A1", selectedSeatsLabel.getText());
    }

    @Test
    public void shouldEnableConfirmButtonWhenSeatSelected(FxRobot robot) {
        Button confirmButton = robot.lookup("#confirmButton").queryAs(Button.class);
        assertTrue(confirmButton.isDisabled());
        
        robot.clickOn(".seat-available");
        assertFalse(confirmButton.isDisabled());
    }

    @Test
    public void shouldShowConfirmationDialog(FxRobot robot) {
        robot.clickOn(".seat-available");
        robot.clickOn("#confirmButton");
        
        // Verify dialog is shown
        assertTrue(robot.lookup(".dialog-pane").tryQuery().isPresent());
    }
} 