package tn.esprit.pidev.services;

import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.ApiResponse;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private Connection connection;

    public EventService() {
        connection = DatabaseConnection.getConnection();
    }

    public List<Evenement> getAllEvents() {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvenement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving events: " + e.getMessage());
        }
        return events;
    }

    public ApiResponse<Boolean> joinEvent(int userId, int eventId, String userRole) {
        String sql = "INSERT INTO jointable (userID, eventID, userRoleInEvent) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            // First check if the event exists and has space
            if (!isEventAvailable(eventId)) {
                return new ApiResponse<>("Event is full or not available");
            }
            
            // Check if user already joined
            if (hasUserJoined(userId, eventId)) {
                return new ApiResponse<>("User already joined this event");
            }
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);
            pstmt.setString(3, userRole);
            pstmt.executeUpdate();
            
            return new ApiResponse<>(true);
        } catch (SQLException e) {
            return new ApiResponse<>("Failed to join event: " + e.getMessage());
        }
    }

    public ApiResponse<List<Evenement>> getMyEvents(int userId) {
        List<Evenement> events = new ArrayList<>();
        String sql = "SELECT e.* FROM evenement e " +
                    "JOIN jointable j ON e.ID = j.eventID " +
                    "WHERE j.userID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvenement(rs));
            }
            
            return new ApiResponse<>(events);
        } catch (SQLException e) {
            return new ApiResponse<>("Failed to fetch events: " + e.getMessage());
        }
    }

    private boolean isEventAvailable(int eventId) throws SQLException {
        String sql = "SELECT participantsMax, " +
                    "(SELECT COUNT(*) FROM jointable WHERE eventID = ?) as currentParticipants, " +
                    "dateDebut, statut " +
                    "FROM evenement WHERE ID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setInt(2, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int maxParticipants = rs.getInt("participantsMax");
                int currentParticipants = rs.getInt("currentParticipants");
                Date eventDate = rs.getDate("dateDebut");
                String status = rs.getString("statut");
                
                return currentParticipants < maxParticipants && 
                       eventDate.toLocalDate().isAfter(java.time.LocalDate.now()) &&
                       !"Annulé".equals(status) &&
                       !"Terminé".equals(status);
            }
            return false;
        }
    }

    private boolean hasUserJoined(int userId, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jointable WHERE userID = ? AND eventID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    private Evenement mapResultSetToEvenement(ResultSet rs) throws SQLException {
        Evenement event = new Evenement();
        event.setId(rs.getLong("ID"));
        event.setNom(rs.getString("nom"));
        event.setDescription(rs.getString("details"));
        event.setType(rs.getString("type"));
        
        // Convert SQL Date to LocalDateTime
        Date dateDebut = rs.getDate("dateDebut");
        Date dateFin = rs.getDate("dateFin");
        if (dateDebut != null) {
            event.setDateDebut(dateDebut.toLocalDate().atStartOfDay());
        }
        if (dateFin != null) {
            event.setDateFin(dateFin.toLocalDate().atStartOfDay());
        }
        
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
                events.add(mapResultSetToEvenement(rs));
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
                return mapResultSetToEvenement(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting event by id: " + e.getMessage());
        }
        return null;
    }

    public boolean addEvent(Evenement event) {
        String sql = "INSERT INTO evenement (nom, details, type, dateDebut, dateFin, participantsMax, statut) VALUES (?, ?, ?, ?, ?, ?, 'En cours')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getType());
            pstmt.setDate(4, Date.valueOf(event.getDateDebut().toLocalDate()));
            pstmt.setDate(5, Date.valueOf(event.getDateFin().toLocalDate()));
            pstmt.setInt(6, event.getCapaciteMax());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEvent(Evenement event) {
        String sql = "UPDATE evenement SET nom = ?, details = ?, type = ?, dateDebut = ?, dateFin = ?, participantsMax = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getType());
            pstmt.setDate(4, Date.valueOf(event.getDateDebut().toLocalDate()));
            pstmt.setDate(5, Date.valueOf(event.getDateFin().toLocalDate()));
            pstmt.setInt(6, event.getCapaciteMax());
            pstmt.setLong(7, event.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM evenement WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            return false;
        }
    }

    public ApiResponse<Boolean> createEvent(Evenement event) {
        String query = "INSERT INTO evenement (nom, description, type, date_debut, date_fin, capacite_max, organisateur_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getType());
            pstmt.setTimestamp(4, Timestamp.valueOf(event.getDateDebut()));
            pstmt.setTimestamp(5, Timestamp.valueOf(event.getDateFin()));
            pstmt.setInt(6, event.getCapaciteMax());
            pstmt.setInt(7, event.getOrganisateurId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Event created successfully", true);
            } else {
                return new ApiResponse<>(false, "Failed to create event", false);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error creating event: " + e.getMessage(), false);
        }
    }
}