package tn.esprit.jappa.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.jappa.models.TerrainSiege;
import tn.esprit.jappa.utils.TestDatabaseInitializer;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TerrainSiegeServiceTest {
    private static Connection connection;
    private TerrainSiegeService terrainSiegeService;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        connection = TestDatabaseInitializer.initializeTestDatabase();
    }

    @BeforeEach
    public void setup() throws Exception {
        // Clear existing data
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM reservation_siege");
            stmt.execute("DELETE FROM terrain_siege");
            stmt.execute("DELETE FROM terrain");
            
            // Insert test terrain
            stmt.execute("INSERT INTO terrain (courtID, type, localisation, capacite, statut) " +
                        "VALUES (1, 'Test Terrain', 'Test Location', 100, 'Disponible')");
            
            // Insert test seats
            stmt.execute("INSERT INTO terrain_siege (siegeID, terrainID, rangee, numero, statut, prix) VALUES " +
                        "(1, 1, 'A', '1', 'Disponible', 25.00), " +
                        "(2, 1, 'A', '2', 'Disponible', 25.00)");
        }
        
        terrainSiegeService = new TerrainSiegeService(connection);
    }

    @Test
    public void getAvailableSeats_ShouldReturnAllSeatsWhenNoReservations() throws Exception {
        String sql = "SELECT ts.* FROM terrain_siege ts " +
                    "LEFT JOIN reservation_siege rs ON ts.siegeID = rs.siegeID " +
                    "LEFT JOIN reservation r ON rs.reservationID = r.ID " +
                    "WHERE ts.terrainID = ? AND ts.statut = 'Disponible' " +
                    "AND (r.dateReservation IS NULL OR DATE(r.dateReservation) != ?)";
        List<TerrainSiege> availableSeats = terrainSiegeService.getAvailableSeats(1, LocalDate.now(), LocalTime.of(14, 0));
        assertEquals(2, availableSeats.size());
    }

    // Add more tests...
} 