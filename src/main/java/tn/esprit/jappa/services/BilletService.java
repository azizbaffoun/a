package tn.esprit.jappa.services;

import tn.esprit.jappa.models.Billet;
import tn.esprit.jappa.models.Event;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BilletService {
    private Connection connection;
    private EventService eventService;

    public BilletService() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
        eventService = new EventService();
    }

    public BilletService(Connection connection) throws SQLException {
        this.connection = connection;
        this.eventService = new EventService(connection);
    }

    public void add(Billet billet) throws SQLException {
        String query = "INSERT INTO billet (eventID, dateAchat, prix, typeBillet, statut, quantite) VALUES (?, NOW(), ?, ?, 'Valide', ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, billet.getEventID());
            pst.setDouble(2, billet.getPrix());
            pst.setString(3, billet.getTypeBillet());
            pst.setInt(4, billet.getQuantite());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                billet.setId(rs.getInt(1));
            }
        }
    }

    public void update(Billet billet) throws SQLException {
        String query = "UPDATE billet SET eventID=?, dateAchat=NOW(), prix=?, typeBillet=?, statut='Valide', quantite=? WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, billet.getEventID());
            pst.setDouble(2, billet.getPrix());
            pst.setString(3, billet.getTypeBillet());
            pst.setInt(4, billet.getQuantite());
            pst.setInt(5, billet.getId());
            pst.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM billet WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<Billet> getAll() throws SQLException {
        List<Billet> billets = new ArrayList<>();
        String query = "SELECT * FROM billet";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Billet billet = new Billet();
                billet.setId(rs.getInt("ID"));
                billet.setEventID(rs.getInt("eventID"));
                billet.setPrix(rs.getDouble("prix"));
                billet.setTypeBillet(rs.getString("typeBillet"));
                billet.setQuantite(rs.getInt("quantite"));
                
                // Load event information
                Event event = eventService.getById(rs.getInt("eventID"));
                billet.setEvent(event);
                
                billets.add(billet);
            }
        }
        return billets;
    }

    public Billet getById(int id) throws SQLException {
        String query = "SELECT * FROM billet WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Billet billet = new Billet();
                billet.setId(rs.getInt("ID"));
                billet.setEventID(rs.getInt("eventID"));
                billet.setPrix(rs.getDouble("prix"));
                billet.setTypeBillet(rs.getString("typeBillet"));
                billet.setQuantite(rs.getInt("quantite"));
                
                // Load event information
                Event event = eventService.getById(rs.getInt("eventID"));
                billet.setEvent(event);
                
                return billet;
            }
        }
        return null;
    }

    public Event getEventById(int eventId) throws SQLException {
        String query = "SELECT * FROM evenement WHERE ID = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Event(
                    rs.getInt("ID"),
                    rs.getString("nom"),
                    rs.getString("details"),
                    rs.getDate("dateDebut").toLocalDate(),
                    rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                    rs.getString("type"),
                    rs.getInt("participantsMax")
                );
            }
        }
        return null;
    }

    public boolean updateQuantity(int billetId, int quantityChange) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // First check current quantity
            String checkQuery = "SELECT quantite FROM billet WHERE ID = ? FOR UPDATE";
            int currentQuantity;
            
            try (PreparedStatement pst = connection.prepareStatement(checkQuery)) {
                pst.setInt(1, billetId);
                ResultSet rs = pst.executeQuery();
                if (!rs.next() || (currentQuantity = rs.getInt("quantite")) + quantityChange < 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Update quantity
            String updateQuery = "UPDATE billet SET quantite = quantite + ? WHERE ID = ? AND quantite + ? >= 0";
            try (PreparedStatement pst = connection.prepareStatement(updateQuery)) {
                pst.setInt(1, quantityChange);
                pst.setInt(2, billetId);
                pst.setInt(3, quantityChange);
                int updated = pst.executeUpdate();
                
                if (updated > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public int getAvailableQuantity(int billetId) throws SQLException {
        String query = "SELECT quantite FROM billet WHERE ID = ? AND statut = 'Valide'";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, billetId);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getInt("quantite") : 0;
        }
    }

    public boolean isQuantityAvailable(int billetId, int requestedQuantity) throws SQLException {
        return getAvailableQuantity(billetId) >= requestedQuantity;
    }

    public boolean reserveTickets(int billetId, int reservationId, int quantity) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Check if enough tickets are available
            if (!isQuantityAvailable(billetId, quantity)) {
                connection.rollback();
                return false;
            }

            // Update ticket quantity
            if (!updateQuantity(billetId, -quantity)) {
                connection.rollback();
                return false;
            }

            // Create reservation record
            String insertQuery = "INSERT INTO reservation_billet (reservationID, billetID, nombreBillet) VALUES (?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(insertQuery)) {
                pst.setInt(1, reservationId);
                pst.setInt(2, billetId);
                pst.setInt(3, quantity);
                pst.executeUpdate();
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

    public boolean cancelReservation(int reservationId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Get reservation details
            String selectQuery = "SELECT billetID, nombreBillet FROM reservation_billet WHERE reservationID = ?";
            int billetId, quantity;
            
            try (PreparedStatement pst = connection.prepareStatement(selectQuery)) {
                pst.setInt(1, reservationId);
                ResultSet rs = pst.executeQuery();
                if (!rs.next()) {
                    connection.rollback();
                    return false;
                }
                billetId = rs.getInt("billetID");
                quantity = rs.getInt("nombreBillet");
            }

            // Return tickets to available pool
            if (!updateQuantity(billetId, quantity)) {
                connection.rollback();
                return false;
            }

            // Delete reservation
            String deleteQuery = "DELETE FROM reservation_billet WHERE reservationID = ?";
            try (PreparedStatement pst = connection.prepareStatement(deleteQuery)) {
                pst.setInt(1, reservationId);
                pst.executeUpdate();
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

    public List<Billet> getTicketsByEvent(int eventId) throws SQLException {
        List<Billet> tickets = new ArrayList<>();
        String query = "SELECT * FROM billet WHERE eventID = ? AND quantite > 0 AND statut = 'Valide'";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Billet billet = mapResultSetToBillet(rs);
                Event event = eventService.getById(eventId);
                billet.setEvent(event);
                tickets.add(billet);
            }
        }
        
        return tickets;
    }

    private Billet mapResultSetToBillet(ResultSet rs) throws SQLException {
        Billet billet = new Billet();
        billet.setId(rs.getInt("ID"));
        billet.setEventID(rs.getInt("eventID"));
        billet.setPrix(rs.getDouble("prix"));
        billet.setTypeBillet(rs.getString("typeBillet"));
        billet.setQuantite(rs.getInt("quantite"));
        billet.setStatut(rs.getString("statut"));
        
        // Load event information
        Event event = eventService.getById(rs.getInt("eventID"));
        billet.setEvent(event);
        
        return billet;
    }
} 