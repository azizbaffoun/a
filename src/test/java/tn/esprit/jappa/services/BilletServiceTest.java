package tn.esprit.jappa.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.jappa.models.Billet;
import tn.esprit.jappa.models.Event;
import tn.esprit.jappa.utils.TestDatabaseInitializer;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BilletServiceTest {
    private static Connection connection;
    private BilletService billetService;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        connection = TestDatabaseInitializer.initializeTestDatabase();
    }

    @BeforeEach
    public void setup() throws Exception {
        // Clear existing data
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM reservation_billet");
            stmt.execute("DELETE FROM billet");
            stmt.execute("DELETE FROM evenement");
            
            // Insert test event
            LocalDate now = LocalDate.now();
            stmt.execute("INSERT INTO evenement (ID, nom, details, dateDebut, dateFin, type, participantsMax) " +
                        "VALUES (1, 'Test Event', 'Test Description', '" + now + "', '" + now.plusDays(7) + "', 'Test', 100)");
            
            // Insert test tickets
            stmt.execute("INSERT INTO billet (ID, eventID, dateAchat, prix, typeBillet, statut, quantite) VALUES " +
                        "(1, 1, NOW(), 100.0, 'VIP', 'Valide', 50), " +
                        "(2, 1, NOW(), 50.0, 'Standard', 'Valide', 100)");
        }
        
        billetService = new BilletService(connection);
    }

    @Test
    public void getAvailableTickets_ShouldReturnAllTicketsWhenNoReservations() throws Exception {
        List<Billet> availableTickets = billetService.getAll();
        assertEquals(2, availableTickets.size());
    }

    // Add more tests...
} 