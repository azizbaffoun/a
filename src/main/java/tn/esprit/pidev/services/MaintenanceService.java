package tn.esprit.pidev.services;

import tn.esprit.pidev.interfaces.IService;
import tn.esprit.pidev.models.Maintenance;
import tn.esprit.pidev.models.MaintenanceStatus;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceService implements IService<Maintenance> {
    private final Connection connection;
    private TodoistService todoistService;
    private MaterielService materielService;

    public MaintenanceService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.todoistService = new TodoistService();
        this.materielService = new MaterielService();
    }

    @Override
    public void add(Maintenance maintenance) throws SQLException {
        String query = "INSERT INTO maintenance (materielID, dateMaintenance, description, statutMaintenance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, maintenance.getMaterielID());
            ps.setDate(2, Date.valueOf(maintenance.getDateMaintenance()));
            ps.setString(3, maintenance.getDescription());
            ps.setString(4, maintenance.getStatutMaintenance().toString());
            ps.executeUpdate();

            // Create Todoist task
            Materiel materiel = materielService.getById(maintenance.getMaterielID());
            todoistService.createMaintenanceTask(maintenance, materiel);
        } catch (SQLException e) {
            System.err.println("Error adding maintenance: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Maintenance maintenance) throws SQLException {
        String query = "UPDATE maintenance SET materielID=?, dateMaintenance=?, description=?, statutMaintenance=? WHERE maintenanceID=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, maintenance.getMaterielID());
            ps.setDate(2, Date.valueOf(maintenance.getDateMaintenance()));
            ps.setString(3, maintenance.getDescription());
            ps.setString(4, maintenance.getStatutMaintenance().toString());
            ps.setInt(5, maintenance.getMaintenanceID());
            ps.executeUpdate();

            // Update Todoist task status
            boolean completed = maintenance.getStatutMaintenance() == MaintenanceStatus.COMPLETED;
            todoistService.updateMaintenanceTaskStatus(maintenance, completed);
        } catch (SQLException e) {
            System.err.println("Error updating maintenance: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM maintenance WHERE maintenanceID=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting maintenance: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Maintenance getById(int id) throws SQLException {
        String query = "SELECT * FROM maintenance WHERE maintenanceID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractMaintenanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting maintenance by ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    @Override
    public List<Maintenance> getAll() throws SQLException {
        List<Maintenance> maintenances = new ArrayList<>();
        String query = "SELECT * FROM maintenance";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                maintenances.add(extractMaintenanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all maintenances: " + e.getMessage());
            throw e;
        }
        return maintenances;
    }

    private Maintenance extractMaintenanceFromResultSet(ResultSet rs) throws SQLException {
        try {
            Maintenance maintenance = new Maintenance();
            maintenance.setMaintenanceID(rs.getInt("maintenanceID"));
            maintenance.setMaterielID(rs.getInt("materielID"));
            maintenance.setDateMaintenance(rs.getDate("dateMaintenance").toLocalDate());
            maintenance.setDescription(rs.getString("description"));
            
            String statusStr = rs.getString("statutMaintenance");
            if (statusStr == null || statusStr.isEmpty()) {
                maintenance.setStatutMaintenance(MaintenanceStatus.PENDING);
            } else {
                try {
                    // Try to parse the enum name directly first
                    maintenance.setStatutMaintenance(MaintenanceStatus.valueOf(statusStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    try {
                        // If that fails, try to parse the display name
                        maintenance.setStatutMaintenance(MaintenanceStatus.fromDisplayName(statusStr));
                    } catch (IllegalArgumentException ex) {
                        // If both fail, default to PENDING
                        System.err.println("Invalid maintenance status '" + statusStr + "', defaulting to PENDING");
                        maintenance.setStatutMaintenance(MaintenanceStatus.PENDING);
                    }
                }
            }
            
            return maintenance;
        } catch (SQLException e) {
            System.err.println("Error extracting maintenance data: " + e.getMessage());
            throw e;
        }
    }

    public List<Maintenance> getMaintenancesByMaterielId(int materielId) {
        List<Maintenance> maintenances = new ArrayList<>();
        String query = "SELECT * FROM maintenance WHERE materielID = ? ORDER BY dateMaintenance DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, materielId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    maintenances.add(extractMaintenanceFromResultSet(rs));
                } catch (SQLException e) {
                    System.err.println("Error extracting maintenance data: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting maintenance records for material: " + e.getMessage());
        }
        
        return maintenances;
    }
} 