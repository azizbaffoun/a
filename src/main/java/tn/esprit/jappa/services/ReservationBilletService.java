package tn.esprit.jappa.services;

import tn.esprit.jappa.models.ReservationBillet;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationBilletService {
    private BilletService billetService;

    public ReservationBilletService() throws SQLException {
        billetService = new BilletService();
    }

    public void add(ReservationBillet reservation) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        
        try {
            // First check if enough tickets are available
            String checkQuery = "SELECT quantite FROM billet WHERE ID = ?";
            try (PreparedStatement checkPst = connection.prepareStatement(checkQuery)) {
                checkPst.setInt(1, reservation.getBilletID());
                ResultSet rs = checkPst.executeQuery();
                
                if (rs.next()) {
                    int availableQuantity = rs.getInt("quantite");
                    if (availableQuantity < reservation.getNombreBillet()) {
                        throw new SQLException("Not enough tickets available");
                    }
                } else {
                    throw new SQLException("Ticket not found");
                }
            }
            
            // Insert reservation
            String insertQuery = "INSERT INTO reservation_billet (reservationID, billetID, nombreBillet) VALUES (?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(insertQuery)) {
                pst.setInt(1, reservation.getReservationID());
                pst.setInt(2, reservation.getBilletID());
                pst.setInt(3, reservation.getNombreBillet());
                pst.executeUpdate();
            }
            
            // Update ticket quantity
            String updateQuery = "UPDATE billet SET quantite = quantite - ? WHERE ID = ?";
            try (PreparedStatement updatePst = connection.prepareStatement(updateQuery)) {
                updatePst.setInt(1, reservation.getNombreBillet());
                updatePst.setInt(2, reservation.getBilletID());
                updatePst.executeUpdate();
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void update(ReservationBillet reservation) throws SQLException {
        String query = "UPDATE reservation_billet SET billetID=?, nombreBillet=? WHERE reservationID=?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, reservation.getBilletID());
            pst.setInt(2, reservation.getNombreBillet());
            pst.setInt(3, reservation.getReservationID());
            pst.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reservation_billet WHERE reservationID=?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<ReservationBillet> getAll() throws SQLException {
        List<ReservationBillet> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation_billet";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                ReservationBillet reservation = new ReservationBillet();
                reservation.setReservationID(rs.getInt("reservationID"));
                reservation.setBilletID(rs.getInt("billetID"));
                reservation.setNombreBillet(rs.getInt("nombreBillet"));
                reservation.setBillet(billetService.getById(rs.getInt("billetID")));
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public ReservationBillet getById(int id) throws SQLException {
        String query = "SELECT * FROM reservation_billet WHERE reservationID=?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ReservationBillet reservation = new ReservationBillet();
                reservation.setReservationID(rs.getInt("reservationID"));
                reservation.setBilletID(rs.getInt("billetID"));
                reservation.setNombreBillet(rs.getInt("nombreBillet"));
                reservation.setBillet(billetService.getById(rs.getInt("billetID")));
                return reservation;
            }
        }
        return null;
    }
} 