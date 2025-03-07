package tn.esprit.pidev.services;

import tn.esprit.pidev.models.Emprunt;
import tn.esprit.pidev.models.Maintenance;
import tn.esprit.pidev.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsService {
    private final Connection connection;
    private final EmpruntService empruntService;
    private final MaintenanceService maintenanceService;

    public AnalyticsService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.empruntService = new EmpruntService();
        this.maintenanceService = new MaintenanceService();
    }

    public Map<String, Integer> getEquipmentUsageStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> usageStats = new HashMap<>();
        String query = "SELECT materielID, COUNT(*) as loan_count FROM emprunt " +
                      "WHERE dateEmprunt BETWEEN ? AND ? " +
                      "GROUP BY materielID";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String materialId = String.valueOf(rs.getInt("materielID"));
                int count = rs.getInt("loan_count");
                usageStats.put(materialId, count);
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment usage stats: " + e.getMessage());
        }
        
        return usageStats;
    }

    public Map<String, Integer> getMaintenanceStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> maintenanceStats = new HashMap<>();
        String query = "SELECT materielID, COUNT(*) as maintenance_count FROM maintenance " +
                      "WHERE dateMaintenance BETWEEN ? AND ? " +
                      "GROUP BY materielID";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String materialId = String.valueOf(rs.getInt("materielID"));
                int count = rs.getInt("maintenance_count");
                maintenanceStats.put(materialId, count);
            }
        } catch (SQLException e) {
            System.err.println("Error getting maintenance stats: " + e.getMessage());
        }
        
        return maintenanceStats;
    }

    public void trackEquipmentLoan(Emprunt emprunt) {
        // This method is now a no-op since we're using direct database queries
    }

    public void trackMaintenance(Maintenance maintenance) {
        // This method is now a no-op since we're using direct database queries
    }
} 