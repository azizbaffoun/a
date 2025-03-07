package tn.esprit.pidev.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.DatabaseMetaData;

import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.ApiResponse;
import tn.esprit.pidev.utils.DatabaseConnection;

public class EventService {
    private Connection connection;

    public EventService() {
        connection = DatabaseConnection.getConnection();
    }

    public List<Evenement> getAllEvents() {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return events;
    }

    public List<Evenement> getMyEvents(int userId) {
        List<Evenement> events = new ArrayList<>();
        
        // First, check if the event_participant table exists
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "event_participant", null);
            
            if (!tables.next()) {
                // Table doesn't exist, create it
                try (Statement stmt = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE event_participant (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id INT," +
                        "event_id INT," +
                        "role VARCHAR(50)," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (event_id) REFERENCES evenement(ID)" +
                        ") ENGINE=InnoDB";
                    stmt.execute(createTableSQL);
                } catch (SQLException e) {
                    System.err.println("Warning: Could not create event_participant table: " + e.getMessage());
                    // Return empty list if we can't create the table
                    return events;
                }
            }
            
            // Now proceed with the query
            String query = "SELECT e.* FROM evenement e " +
                         "JOIN event_participant ep ON e.ID = ep.event_id " +
                         "WHERE ep.user_id = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user events: " + e.getMessage());
            e.printStackTrace();
        }
        
        return events;
    }

    public ApiResponse<Boolean> joinEvent(int userId, int eventId, String role) {
        // First check if the event_participant table exists
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "event_participant", null);
            
            if (!tables.next()) {
                // Table doesn't exist, create it
                try (Statement stmt = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE event_participant (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id INT," +
                        "event_id INT," +
                        "role VARCHAR(50)," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (event_id) REFERENCES evenement(ID)" +
                        ") ENGINE=InnoDB";
                    stmt.execute(createTableSQL);
                } catch (SQLException e) {
                    return new ApiResponse<>(false, "Could not create event_participant table: " + e.getMessage(), false);
                }
            }
        } catch (SQLException e) {
            return new ApiResponse<>(false, "Database error: " + e.getMessage(), false);
        }

        // Now proceed with the insert
        String query = "INSERT INTO event_participant (user_id, event_id, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);
            pstmt.setString(3, role);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Successfully joined the event", true);
            } else {
                return new ApiResponse<>(false, "Failed to join the event", false);
            }
        } catch (SQLException e) {
            return new ApiResponse<>(false, "Error joining event: " + e.getMessage(), false);
        }
    }

    public ApiResponse<Boolean> createEvent(Evenement event) {
        String query = "INSERT INTO evenement (nom, details, type, dateDebut, dateFin, participantsMax, statut) " +
                      "VALUES (?, ?, ?, ?, ?, ?, 'En cours')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getType());
            pstmt.setDate(4, java.sql.Date.valueOf(event.getDateDebut().toLocalDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(event.getDateFin().toLocalDate()));
            pstmt.setInt(6, event.getCapaciteMax());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return new ApiResponse<Boolean>(true, "Event created successfully", true);
            } else {
                return new ApiResponse<Boolean>(false, "Failed to create event", false);
            }
        } catch (SQLException e) {
            return new ApiResponse<Boolean>(false, "Error creating event: " + e.getMessage(), false);
        }
    }

    private Evenement mapResultSetToEvent(ResultSet rs) throws SQLException {
        Evenement event = new Evenement();
        event.setId(rs.getLong("ID"));
        event.setNom(rs.getString("nom"));
        event.setDescription(rs.getString("details"));
        event.setType(rs.getString("type"));
        event.setDateDebut(rs.getDate("dateDebut").toLocalDate().atStartOfDay());
        event.setDateFin(rs.getDate("dateFin").toLocalDate().atStartOfDay());
        event.setCapaciteMax(rs.getInt("participantsMax"));
        event.setStatut(rs.getString("statut"));
        event.setRecompense(rs.getString("recompense"));
        return event;
    }

    public List<Evenement> searchEvents(String searchTerm) {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE nom LIKE ? OR details LIKE ? OR type LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching events: " + e.getMessage());
        }
        return events;
    }

    public Evenement getEventById(int id) {
        String query = "SELECT * FROM evenement WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEvent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting event by id: " + e.getMessage());
        }
        return null;
    }

    public boolean addEvent(Evenement event) {
        if (event == null) {
            System.err.println("Error adding event: Event object is null");
            return false;
        }

        String sql = "INSERT INTO evenement (nom, details, type, dateDebut, dateFin, participantsMax, statut) VALUES (?, ?, ?, ?, ?, ?, 'En cours')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Validate required fields
            if (event.getNom() == null || event.getNom().trim().isEmpty()) {
                System.err.println("Error adding event: Name is required");
                return false;
            }
            if (event.getType() == null || event.getType().trim().isEmpty()) {
                System.err.println("Error adding event: Type is required");
                return false;
            }
            // Validate type enum
            String type = event.getType().trim().toUpperCase();
            if (!type.equals("TERRAIN") && !type.equals("PADDEL")) {
                System.err.println("Error adding event: Type must be either TERRAIN or PADDEL");
                return false;
            }
            if (event.getDateDebut() == null) {
                System.err.println("Error adding event: Start date is required");
                return false;
            }
            if (event.getDateFin() == null) {
                System.err.println("Error adding event: End date is required");
                return false;
            }
            if (event.getCapaciteMax() <= 0) {
                System.err.println("Error adding event: Max participants must be positive");
                return false;
            }

            pstmt.setString(1, event.getNom().trim());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, type); // Use validated type
            pstmt.setDate(4, java.sql.Date.valueOf(event.getDateDebut().toLocalDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(event.getDateFin().toLocalDate()));
            pstmt.setInt(6, event.getCapaciteMax());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Event added successfully: " + event.getNom());
                return true;
            } else {
                System.err.println("Error adding event: No rows affected");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error adding event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEvent(Evenement event) {
        if (event == null) {
            System.err.println("Error updating event: Event object is null");
            return false;
        }

        String sql = "UPDATE evenement SET nom = ?, details = ?, type = ?, dateDebut = ?, dateFin = ?, participantsMax = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Validate required fields
            if (event.getNom() == null || event.getNom().trim().isEmpty()) {
                System.err.println("Error updating event: Name is required");
                return false;
            }
            if (event.getType() == null || event.getType().trim().isEmpty()) {
                System.err.println("Error updating event: Type is required");
                return false;
            }
            // Validate type enum
            String type = event.getType().trim().toUpperCase();
            if (!type.equals("TERRAIN") && !type.equals("PADDEL")) {
                System.err.println("Error updating event: Type must be either TERRAIN or PADDEL");
                return false;
            }
            if (event.getDateDebut() == null) {
                System.err.println("Error updating event: Start date is required");
                return false;
            }
            if (event.getDateFin() == null) {
                System.err.println("Error updating event: End date is required");
                return false;
            }
            if (event.getCapaciteMax() <= 0) {
                System.err.println("Error updating event: Max participants must be positive");
                return false;
            }

            pstmt.setString(1, event.getNom().trim());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, type); // Use validated type
            pstmt.setDate(4, java.sql.Date.valueOf(event.getDateDebut().toLocalDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(event.getDateFin().toLocalDate()));
            pstmt.setInt(6, event.getCapaciteMax());
            pstmt.setLong(7, event.getId());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Event updated successfully: " + event.getNom());
                return true;
            } else {
                System.err.println("Error updating event: No rows affected");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error updating event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(int id) {
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            try {
                // First delete related records from event_participant table
                String deleteParticipantsSQL = "DELETE FROM event_participant WHERE event_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteParticipantsSQL)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }

                // Delete related records from billet table
                String deleteBilletsSQL = "DELETE FROM billet WHERE eventID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteBilletsSQL)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }

                // Finally delete the event
                String deleteEventSQL = "DELETE FROM evenement WHERE ID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteEventSQL)) {
                    pstmt.setInt(1, id);
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        // If everything succeeded, commit the transaction
                        connection.commit();
                        System.out.println("Event and related records deleted successfully");
                        return true;
                    } else {
                        // If no event was deleted, rollback
                        connection.rollback();
                        System.err.println("Error deleting event: Event not found");
                        return false;
                    }
                }
            } catch (SQLException e) {
                // If any error occurred, rollback the transaction
                connection.rollback();
                System.err.println("Error deleting event: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                // Reset auto-commit to true
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Transaction error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}