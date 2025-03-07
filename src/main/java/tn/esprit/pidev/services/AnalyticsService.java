package tn.esprit.pidev.services;

import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.Venue;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalyticsService {
    private final Connection connection;

    public AnalyticsService(Connection connection) {
        this.connection = connection;
    }

    public Map<String, Integer> getVenueUsageStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT v.type, COUNT(e.ID) as usage_count " +
                      "FROM venue v LEFT JOIN evenement e ON v.ID = e.venueID " +
                      "GROUP BY v.type";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("type"), rs.getInt("usage_count"));
            }
        }
        return stats;
    }

    public Map<DayOfWeek, Integer> getBookingsByDayOfWeek() throws SQLException {
        Map<DayOfWeek, Integer> bookings = new HashMap<>();
        String query = "SELECT DAYOFWEEK(dateDebut) as day, COUNT(*) as count " +
                      "FROM evenement GROUP BY DAYOFWEEK(dateDebut)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int day = rs.getInt("day");
                bookings.put(DayOfWeek.of(day), rs.getInt("count"));
            }
        }
        return bookings;
    }

    public Map<Integer, Double> getRevenueByHour() throws SQLException {
        Map<Integer, Double> revenue = new HashMap<>();
        String query = "SELECT HOUR(dateDebut) as hour, SUM(prix) as total " +
                      "FROM evenement GROUP BY HOUR(dateDebut)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                revenue.put(rs.getInt("hour"), rs.getDouble("total"));
            }
        }
        return revenue;
    }

    public double calculateVenueUtilization(long venueId, LocalDateTime start, LocalDateTime end) throws SQLException {
        String query = "SELECT COUNT(*) as total_hours FROM evenement " +
                      "WHERE venueID = ? AND dateDebut >= ? AND dateFin <= ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, venueId);
            stmt.setObject(2, start);
            stmt.setObject(3, end);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long totalHours = rs.getLong("total_hours");
                long availableHours = start.until(end, java.time.temporal.ChronoUnit.HOURS);
                return (double) totalHours / availableHours;
            }
        }
        return 0.0;
    }

    public Map<String, Object> generateVenueReport(long venueId) throws SQLException {
        Map<String, Object> report = new HashMap<>();
        
        // Get basic venue info
        String venueQuery = "SELECT * FROM venue WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(venueQuery)) {
            stmt.setLong(1, venueId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                report.put("type", rs.getString("type"));
                report.put("capacity", rs.getInt("capacite"));
                report.put("location", rs.getString("localisation"));
            }
        }

        // Calculate utilization
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);
        double utilization = calculateVenueUtilization(venueId, monthAgo, now);
        report.put("utilization", utilization);

        // Get revenue data
        String revenueQuery = "SELECT SUM(prix) as total_revenue, " +
                            "COUNT(*) as total_events, " +
                            "AVG(prix) as avg_price " +
                            "FROM evenement WHERE venueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(revenueQuery)) {
            stmt.setLong(1, venueId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                report.put("totalRevenue", rs.getDouble("total_revenue"));
                report.put("totalEvents", rs.getInt("total_events"));
                report.put("averagePrice", rs.getDouble("avg_price"));
            }
        }

        return report;
    }

    public List<Map<String, Object>> getPeakHours() throws SQLException {
        List<Map<String, Object>> peakHours = new ArrayList<>();
        String query = "SELECT HOUR(dateDebut) as hour, " +
                      "COUNT(*) as booking_count " +
                      "FROM evenement " +
                      "GROUP BY HOUR(dateDebut) " +
                      "ORDER BY booking_count DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", rs.getInt("hour"));
                hourData.put("bookings", rs.getInt("booking_count"));
                peakHours.add(hourData);
            }
        }
        return peakHours;
    }

    public Map<String, Double> calculateRevenueMetrics() throws SQLException {
        Map<String, Double> metrics = new HashMap<>();
        
        // Revenue per square meter (assuming venue has area field)
        String revenuePerAreaQuery = "SELECT SUM(e.prix) / SUM(v.area) as revenue_per_sqm " +
                                   "FROM evenement e JOIN venue v ON e.venueID = v.ID";
        
        // Average revenue per booking
        String avgRevenueQuery = "SELECT AVG(prix) as avg_revenue FROM evenement";
        
        // Revenue growth (comparing current month to previous month)
        String growthQuery = "SELECT " +
            "(SELECT SUM(prix) FROM evenement WHERE MONTH(dateDebut) = MONTH(CURRENT_DATE)) as current_month, " +
            "(SELECT SUM(prix) FROM evenement WHERE MONTH(dateDebut) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)) as prev_month";
        
        try (PreparedStatement stmt1 = connection.prepareStatement(revenuePerAreaQuery);
             PreparedStatement stmt2 = connection.prepareStatement(avgRevenueQuery);
             PreparedStatement stmt3 = connection.prepareStatement(growthQuery)) {
            
            // Revenue per square meter
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                metrics.put("revenuePerSqm", rs1.getDouble("revenue_per_sqm"));
            }
            
            // Average revenue per booking
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                metrics.put("avgRevenue", rs2.getDouble("avg_revenue"));
            }
            
            // Revenue growth
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) {
                double currentMonth = rs3.getDouble("current_month");
                double prevMonth = rs3.getDouble("prev_month");
                double growth = prevMonth > 0 ? ((currentMonth - prevMonth) / prevMonth) * 100 : 0;
                metrics.put("revenueGrowth", growth);
            }
        }
        
        return metrics;
    }
} 