package tn.esprit.jappa.services;

import tn.esprit.jappa.interfaces.IService;
import tn.esprit.jappa.models.Maintenance;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceService implements IService<Maintenance> {
    private Connection connection;

    public MaintenanceService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void add(Maintenance maintenance) throws SQLException {
        String query = "INSERT INTO maintenance (materielID, dateMaintenance, description, statutMaintenance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, maintenance.getMaterielID());
            pst.setString(2, maintenance.getDateMaintenance());
            pst.setString(3, maintenance.getDescription());
            pst.setString(4, maintenance.getStatutMaintenance());
            pst.executeUpdate();
        }
    }

    @Override
    public void update(Maintenance maintenance) throws SQLException {
        String query = "UPDATE maintenance SET materielID=?, dateMaintenance=?, description=?, statutMaintenance=? WHERE maintenanceID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, maintenance.getMaterielID());
            pst.setString(2, maintenance.getDateMaintenance());
            pst.setString(3, maintenance.getDescription());
            pst.setString(4, maintenance.getStatutMaintenance());
            pst.setInt(5, maintenance.getMaintenanceID());
            pst.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM maintenance WHERE maintenanceID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
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
        maintenance.setMaintenanceID(rs.getInt("maintenanceID"));
        maintenance.setMaterielID(rs.getInt("materielID"));
        maintenance.setDateMaintenance(rs.getString("dateMaintenance"));
        maintenance.setDescription(rs.getString("description"));
        maintenance.setStatutMaintenance(rs.getString("statutMaintenance"));
        return maintenance;
    }
} 