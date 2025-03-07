package tn.esprit.pidev.services;

import tn.esprit.pidev.interfaces.IService;
import tn.esprit.pidev.models.Emprunt;
import tn.esprit.pidev.models.EmpruntStatus;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntService implements IService<Emprunt> {
    private final Connection connection;
    private TodoistService todoistService;
    private MaterielService materielService;

    public EmpruntService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.todoistService = new TodoistService();
        this.materielService = new MaterielService();
    }

    @Override
    public void add(Emprunt emprunt) throws SQLException {
        String query = "INSERT INTO emprunt (userID, materielID, dateEmprunt, dateRetour, statutEmprunt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, emprunt.getUserID());
            ps.setInt(2, emprunt.getMaterielID());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(emprunt.getDateRetour()));
            ps.setString(5, emprunt.getStatutEmprunt().name());
            ps.executeUpdate();

            // Create Todoist task
            Materiel materiel = materielService.getById(emprunt.getMaterielID());
            todoistService.createLoanTask(emprunt, materiel);
        }
    }

    @Override
    public void update(Emprunt emprunt) throws SQLException {
        String query = "UPDATE emprunt SET userID=?, materielID=?, dateEmprunt=?, dateRetour=?, statutEmprunt=? WHERE empruntID=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, emprunt.getUserID());
            ps.setInt(2, emprunt.getMaterielID());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(emprunt.getDateRetour()));
            ps.setString(5, emprunt.getStatutEmprunt().name());
            ps.setInt(6, emprunt.getEmpruntID());
            ps.executeUpdate();

            // Update Todoist task status
            boolean completed = emprunt.getStatutEmprunt() == EmpruntStatus.RETURNED;
            todoistService.updateLoanTaskStatus(emprunt, completed);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM emprunt WHERE empruntID=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Emprunt getById(int id) throws SQLException {
        String query = "SELECT * FROM emprunt WHERE empruntID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractEmpruntFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Emprunt> getAll() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String query = "SELECT * FROM emprunt";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
        }
        return emprunts;
    }

    private Emprunt extractEmpruntFromResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setEmpruntID(rs.getInt("empruntID"));
        emprunt.setUserID(rs.getInt("userID"));
        emprunt.setMaterielID(rs.getInt("materielID"));
        emprunt.setDateEmprunt(rs.getDate("dateEmprunt").toLocalDate());
        emprunt.setDateRetour(rs.getDate("dateRetour").toLocalDate());
        String statusStr = rs.getString("statutEmprunt");
        try {
            emprunt.setStatutEmprunt(EmpruntStatus.fromDisplayName(statusStr));
        } catch (IllegalArgumentException e) {
            // If display name fails, try direct enum name
            emprunt.setStatutEmprunt(EmpruntStatus.valueOf(statusStr));
        }
        return emprunt;
    }

    public boolean isDateTaken(int materielID, LocalDate date) throws SQLException {
        String query = "SELECT COUNT(*) FROM emprunt WHERE materielID = ? AND dateEmprunt <= ? AND dateRetour >= ? AND statutEmprunt != 'CANCELLED'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, materielID);
            ps.setDate(2, Date.valueOf(date));
            ps.setDate(3, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int getLoanCount(int materielID) throws SQLException {
        String query = "SELECT COUNT(*) FROM emprunt WHERE materielID = ? AND statutEmprunt != 'CANCELLED'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, materielID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Emprunt> getEmpruntsByMaterielId(int materielId) {
        List<Emprunt> emprunts = new ArrayList<>();
        String query = "SELECT * FROM emprunt WHERE materielID = ? ORDER BY dateEmprunt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, materielId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    emprunts.add(extractEmpruntFromResultSet(rs));
                } catch (SQLException e) {
                    System.err.println("Error extracting loan data: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting loans for material: " + e.getMessage());
        }
        
        return emprunts;
    }
} 