package tn.esprit.jappa.services;

import tn.esprit.jappa.models.Reservation;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private Connection connection;

    public ReservationService() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public int add(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (utilisateurID, dateReservation, statut) VALUES (?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reservation.getUtilisateurID());
            pst.setDate(2, Date.valueOf(reservation.getDateReservation()));
            pst.setString(3, reservation.getStatut());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                reservation.setId(generatedId);
                return generatedId;
            }
            throw new SQLException("Failed to get generated ID for reservation");
        }
    }

    public void update(Reservation reservation) throws SQLException {
        String query = "UPDATE reservation SET utilisateurID=?, dateReservation=?, statut=? WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, reservation.getUtilisateurID());
            pst.setDate(2, Date.valueOf(reservation.getDateReservation()));
            pst.setString(3, reservation.getStatut());
            pst.setInt(4, reservation.getId());
            pst.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reservation WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<Reservation> getAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("ID"));
                reservation.setUtilisateurID(rs.getInt("utilisateurID"));
                reservation.setDateReservation(rs.getDate("dateReservation").toLocalDate());
                reservation.setStatut(rs.getString("statut"));
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public Reservation getById(int id) throws SQLException {
        String query = "SELECT * FROM reservation WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("ID"));
                reservation.setUtilisateurID(rs.getInt("utilisateurID"));
                reservation.setDateReservation(rs.getDate("dateReservation").toLocalDate());
                reservation.setStatut(rs.getString("statut"));
                return reservation;
            }
        }
        return null;
    }
} 