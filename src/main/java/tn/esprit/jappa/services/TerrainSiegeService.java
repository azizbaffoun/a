package tn.esprit.jappa.services;

import tn.esprit.jappa.models.TerrainSiege;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TerrainSiegeService {
    private Connection connection;

    public TerrainSiegeService(Connection connection) {
        this.connection = connection;
    }

    public List<TerrainSiege> getAvailableSeats(int terrainId, LocalDate date, LocalTime time) throws SQLException {
        List<TerrainSiege> seats = new ArrayList<>();
        String query = """
            SELECT ts.* FROM terrain_siege ts
            LEFT JOIN reservation_siege rs ON ts.siegeID = rs.siegeID
            LEFT JOIN reservation r ON rs.reservationID = r.ID
            WHERE ts.terrainID = ? AND (r.ID IS NULL OR 
                  r.dateReservation != ?)
            AND ts.statut = 'Disponible'
            ORDER BY ts.rangee, ts.numero
        """;
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, terrainId);
            pst.setDate(2, Date.valueOf(date));
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                TerrainSiege siege = new TerrainSiege(
                    rs.getInt("siegeID"),
                    rs.getInt("terrainID"),
                    rs.getString("rangee"),
                    rs.getString("numero"),
                    rs.getString("statut"),
                    rs.getDouble("prix")
                );
                seats.add(siege);
            }
        }
        return seats;
    }

    public boolean reserveSeats(int reservationId, List<TerrainSiege> seats) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String insertQuery = "INSERT INTO reservation_siege (reservationID, siegeID, prix_unitaire) VALUES (?, ?, ?)";
            String updateQuery = "UPDATE terrain_siege SET statut = 'Réservé' WHERE siegeID = ?";
            
            try (PreparedStatement insertPst = connection.prepareStatement(insertQuery);
                 PreparedStatement updatePst = connection.prepareStatement(updateQuery)) {
                
                for (TerrainSiege siege : seats) {
                    // Insert reservation
                    insertPst.setInt(1, reservationId);
                    insertPst.setInt(2, siege.getSiegeID());
                    insertPst.setDouble(3, siege.getPrix());
                    insertPst.executeUpdate();
                    
                    // Update seat status
                    updatePst.setInt(1, siege.getSiegeID());
                    updatePst.executeUpdate();
                }
            }
            
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void updateSeatStatus(int siegeId, String status) throws SQLException {
        String query = "UPDATE terrain_siege SET statut = ? WHERE siegeID = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setInt(2, siegeId);
            pst.executeUpdate();
        }
    }

    public List<TerrainSiege> getSeatsForReservation(int reservationId) throws SQLException {
        List<TerrainSiege> seats = new ArrayList<>();
        String query = """
            SELECT ts.*, rs.prix_unitaire 
            FROM terrain_siege ts
            JOIN reservation_siege rs ON ts.siegeID = rs.siegeID
            WHERE rs.reservationID = ?
        """;
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, reservationId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                TerrainSiege siege = new TerrainSiege(
                    rs.getInt("siegeID"),
                    rs.getInt("terrainID"),
                    rs.getString("rangee"),
                    rs.getString("numero"),
                    rs.getString("statut"),
                    rs.getDouble("prix_unitaire")
                );
                seats.add(siege);
            }
        }
        return seats;
    }
} 