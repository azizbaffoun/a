package tn.esprit.jappa.controllers;

import tn.esprit.jappa.models.Billet;
import tn.esprit.jappa.services.BilletService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class BilletControllerTest {
    private BilletController controller;
    private BilletService billetService;
    private Connection connection;

    @Start
    private void start(Stage stage) throws Exception {
        // Setup database connection
        connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/jawher_db_test",
            "root",
            ""
        );
        
        // Create test tables and data
        setupTestData();
        
        // Initialize service
        billetService = new BilletService(connection);
        
        // Load FXML
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/fxml/billet_reservation.fxml")
        );
        Scene scene = new Scene(loader.load());
        controller = loader.getController();
        
        // Initialize controller
        controller.initialize(billetService);
        
        stage.setScene(scene);
        stage.show();
    }

    private void setupTestData() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            // Drop existing tables
            stmt.execute("DROP TABLE IF EXISTS reservation_billet");
            stmt.execute("DROP TABLE IF EXISTS billet");
            
            // Create billet table
            stmt.execute("""
                CREATE TABLE billet (
                    ID INT PRIMARY KEY AUTO_INCREMENT,
                    eventID INT,
                    dateAchat DATETIME,
                    prix DECIMAL(10,2),
                    typeBillet VARCHAR(50),
                    statut ENUM('Valide', 'Annulé', 'Non valide') DEFAULT 'Valide',
                    quantite INT DEFAULT 1
                )
            """);
            
            // Create reservation_billet table
            stmt.execute("""
                CREATE TABLE reservation_billet (
                    reservationID INT,
                    billetID INT,
                    nombreBillet INT NOT NULL DEFAULT 1,
                    PRIMARY KEY (reservationID, billetID),
                    FOREIGN KEY (billetID) REFERENCES billet(ID)
                )
            """);
            
            // Insert test data
            stmt.execute("""
                INSERT INTO billet (ID, eventID, dateAchat, prix, typeBillet, statut, quantite) VALUES
                (1, 1, NOW(), 25.00, 'Standard', 'Valide', 10),
                (2, 1, NOW(), 35.00, 'VIP', 'Valide', 5)
            """);
        }
    }

    @Test
    void shouldDisableReservationFormInitially(FxRobot robot) {
        VBox reservationForm = robot.lookup("#reservationForm").queryAs(VBox.class);
        assertThat(reservationForm).isDisabled();
    }

    @Test
    void shouldEnableReservationFormWhenTicketSelected(FxRobot robot) {
        // Select a ticket
        ComboBox<Billet> ticketComboBox = robot.lookup("#ticketComboBox").queryAs(ComboBox.class);
        robot.interact(() -> ticketComboBox.getSelectionModel().select(0));
        
        // Verify form is enabled
        VBox reservationForm = robot.lookup("#reservationForm").queryAs(VBox.class);
        assertThat(reservationForm).isEnabled();
    }

    @Test
    void shouldUpdateTotalWhenQuantityChanged(FxRobot robot) {
        // Select a ticket
        ComboBox<Billet> ticketComboBox = robot.lookup("#ticketComboBox").queryAs(ComboBox.class);
        robot.interact(() -> ticketComboBox.getSelectionModel().select(0));
        
        // Change quantity
        Spinner<Integer> quantitySpinner = robot.lookup("#quantitySpinner").queryAs(Spinner.class);
        robot.interact(() -> quantitySpinner.getValueFactory().setValue(2));
        
        // Verify total is updated
        Label totalLabel = robot.lookup("#totalPriceLabel").queryAs(Label.class);
        assertEquals("Total: 50.00€", totalLabel.getText());
    }

    @Test
    void shouldShowConfirmationDialogOnReserve(FxRobot robot) {
        // Select a ticket
        ComboBox<Billet> ticketComboBox = robot.lookup("#ticketComboBox").queryAs(ComboBox.class);
        robot.interact(() -> ticketComboBox.getSelectionModel().select(0));
        
        // Click reserve button
        robot.clickOn("#reserveButton");
        
        // Verify confirmation dialog is shown
        robot.lookup(".dialog-pane").tryQuery().isPresent();
    }

    @Test
    void shouldUpdateAvailableQuantityLabel(FxRobot robot) {
        // Select a ticket
        ComboBox<Billet> ticketComboBox = robot.lookup("#ticketComboBox").queryAs(ComboBox.class);
        robot.interact(() -> ticketComboBox.getSelectionModel().select(0));
        
        // Verify available quantity is shown
        Label availableQuantityLabel = robot.lookup("#availableQuantityLabel").queryAs(Label.class);
        assertEquals("Available: 10", availableQuantityLabel.getText());
    }

    @Test
    void shouldLimitQuantitySpinnerToAvailable(FxRobot robot) {
        // Select a ticket
        ComboBox<Billet> ticketComboBox = robot.lookup("#ticketComboBox").queryAs(ComboBox.class);
        robot.interact(() -> ticketComboBox.getSelectionModel().select(0));
        
        // Get spinner
        Spinner<Integer> quantitySpinner = robot.lookup("#quantitySpinner").queryAs(Spinner.class);
        
        // Verify max value is limited to available quantity
        assertEquals(10, ((SpinnerValueFactory.IntegerSpinnerValueFactory)
            quantitySpinner.getValueFactory()).getMax());
    }
} 