package tn.esprit.pidev.services;

import tn.esprit.pidev.interfaces.IEventParticipantService;
import tn.esprit.pidev.models.EventParticipant;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventParticipantService implements IEventParticipantService {
    private Connection connection;

    public EventParticipantService() {
        connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean addParticipant(EventParticipant participant) {
        String query = "INSERT INTO jointable (userID, eventID, userRoleInEvent) VALUES (?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, participant.getUserID());
            pst.setInt(2, participant.getEventID());
            pst.setString(3, participant.getUserRoleInEvent());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding participant: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeParticipant(int userId, int eventId) {
        String query = "DELETE FROM jointable WHERE userID=? AND eventID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, eventId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing participant: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateParticipantRole(EventParticipant participant) {
        String query = "UPDATE jointable SET userRoleInEvent=? WHERE userID=? AND eventID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, participant.getUserRoleInEvent());
            pst.setInt(2, participant.getUserID());
            pst.setInt(3, participant.getEventID());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating participant role: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<EventParticipant> getEventParticipants(int eventId) {
        List<EventParticipant> participants = new ArrayList<>();
        String query = "SELECT * FROM jointable WHERE eventID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                participants.add(mapResultSetToEventParticipant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving event participants: " + e.getMessage());
        }
        return participants;
    }

    @Override
    public List<EventParticipant> getUserEvents(int userId) {
        List<EventParticipant> events = new ArrayList<>();
        String query = "SELECT * FROM jointable WHERE userID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEventParticipant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user events: " + e.getMessage());
        }
        return events;
    }

    private EventParticipant mapResultSetToEventParticipant(ResultSet rs) throws SQLException {
        EventParticipant participant = new EventParticipant();
        participant.setUserID(rs.getInt("userID"));
        participant.setEventID(rs.getInt("eventID"));
        participant.setUserRoleInEvent(rs.getString("userRoleInEvent"));
        return participant;
    }
} 