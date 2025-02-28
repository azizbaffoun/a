package tn.esprit.pidev.services;

import tn.esprit.pidev.interfaces.IVenueService;
import tn.esprit.pidev.models.Venue;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class VenueService implements IVenueService {
    private Connection connection;

    public VenueService() {
        connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean addVenue(Venue venue) {
        String query = "INSERT INTO terrain (type, localisation, capacite, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, venue.getType());
            pst.setString(2, venue.getLocalisation());
            pst.setInt(3, venue.getCapacite());
            pst.setString(4, venue.getStatut());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding venue: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateVenue(Venue venue) {
        String query = "UPDATE terrain SET type=?, localisation=?, capacite=?, statut=? WHERE courtID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, venue.getType());
            pst.setString(2, venue.getLocalisation());
            pst.setInt(3, venue.getCapacite());
            pst.setString(4, venue.getStatut());
            pst.setInt(5, venue.getCourtID());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating venue: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteVenue(int venueId) {
        String query = "DELETE FROM terrain WHERE courtID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, venueId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting venue: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Venue getVenueById(int venueId) {
        String query = "SELECT * FROM terrain WHERE courtID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, venueId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVenue(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving venue: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String query = "SELECT * FROM terrain";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving venues: " + e.getMessage());
        }
        return venues;
    }

    @Override
    public List<Venue> getAvailableVenues() {
        List<Venue> venues = new ArrayList<>();
        String query = "SELECT * FROM terrain WHERE statut = 'Disponible'";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving available venues: " + e.getMessage());
        }
        return venues;
    }

    @Override
    public boolean checkVenueAvailability(int venueId, Date date) {
        String query = "SELECT statut FROM terrain WHERE courtID = ? AND statut = 'Disponible'";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, venueId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking venue availability: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateVenueStatus(int venueId, String status) {
        String query = "UPDATE terrain SET statut = ? WHERE courtID = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setInt(2, venueId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating venue status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scheduleVenueMaintenance(int venueId, Date startDate, Date endDate) {
        // First check if the venue exists and is available
        if (!checkVenueAvailability(venueId, startDate)) {
            return false;
        }
        
        // Update the venue status to Maintenance
        return updateVenueStatus(venueId, "Maintenance");
    }

    private Venue mapResultSetToVenue(ResultSet rs) throws SQLException {
        Venue venue = new Venue();
        venue.setCourtID(rs.getInt("courtID"));
        venue.setType(rs.getString("type"));
        venue.setLocalisation(rs.getString("localisation"));
        venue.setCapacite(rs.getInt("capacite"));
        venue.setStatut(rs.getString("statut"));
        return venue;
    }
} 