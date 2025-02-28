package tn.esprit.jappa.services;

import tn.esprit.jappa.interfaces.IService;
import tn.esprit.jappa.models.Maintenance;
import tn.esprit.jappa.models.MaintenanceStatus;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceService implements IService<Maintenance> {
    private Connection connection;

    public MaintenanceService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void add(Maintenance maintenance) throws SQLException {
        String query = "INSERT INTO maintenance (materiel_id, date_maintenance, description, cout, statut_maintenance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, maintenance.getMaterielID());
            ps.setDate(2, Date.valueOf(maintenance.getDateMaintenance()));
            ps.setString(3, maintenance.getDescription());
            ps.setDouble(4, maintenance.getCout());
            ps.setString(5, maintenance.getStatutMaintenance().name());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Maintenance maintenance) throws SQLException {
        String query = "UPDATE maintenance SET materiel_id=?, date_maintenance=?, description=?, cout=?, statut_maintenance=? WHERE maintenance_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, maintenance.getMaterielID());
            ps.setDate(2, Date.valueOf(maintenance.getDateMaintenance()));
            ps.setString(3, maintenance.getDescription());
            ps.setDouble(4, maintenance.getCout());
            ps.setString(5, maintenance.getStatutMaintenance().name());
            ps.setInt(6, maintenance.getMaintenanceID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM maintenance WHERE maintenance_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Maintenance getById(int id) throws SQLException {
        String query = "SELECT * FROM maintenance WHERE maintenance_id=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractMaintenanceFromResultSet(rs);
            }
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
        }
        return maintenances;
    }

    private Maintenance extractMaintenanceFromResultSet(ResultSet rs) throws SQLException {
        Maintenance maintenance = new Maintenance();
        maintenance.setMaintenanceID(rs.getInt("maintenance_id"));
        maintenance.setMaterielID(rs.getInt("materiel_id"));
        maintenance.setDateMaintenance(rs.getDate("date_maintenance").toLocalDate());
        maintenance.setDescription(rs.getString("description"));
        maintenance.setCout(rs.getDouble("cout"));
        maintenance.setStatutMaintenance(MaintenanceStatus.valueOf(rs.getString("statut_maintenance")));
        return maintenance;
    }
} 