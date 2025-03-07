package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import tn.esprit.pidev.services.*;
import tn.esprit.pidev.models.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

public class StatisticsController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private BarChart<String, Number> usageChart;
    @FXML private PieChart maintenanceChart;
    @FXML private TableView<EquipmentUsageStats> usageTable;
    @FXML private TableView<MaintenanceStats> maintenanceTable;
    
    @FXML private TableColumn<EquipmentUsageStats, String> equipmentIdColumn;
    @FXML private TableColumn<EquipmentUsageStats, String> equipmentTypeColumn;
    @FXML private TableColumn<EquipmentUsageStats, Integer> loanCountColumn;
    @FXML private TableColumn<EquipmentUsageStats, String> lastLoanColumn;
    
    @FXML private TableColumn<MaintenanceStats, String> maintEquipmentIdColumn;
    @FXML private TableColumn<MaintenanceStats, Integer> maintenanceCountColumn;
    @FXML private TableColumn<MaintenanceStats, String> lastMaintenanceColumn;
    @FXML private TableColumn<MaintenanceStats, String> statusColumn;

    private final AnalyticsService analyticsService;
    private final MaterielService materielService;
    private final MaintenanceService maintenanceService;
    private final EmpruntService empruntService;

    public StatisticsController() {
        this.analyticsService = new AnalyticsService();
        this.materielService = new MaterielService();
        this.maintenanceService = new MaintenanceService();
        this.empruntService = new EmpruntService();
    }

    @FXML
    private void initialize() {
        // Set default date range to last 30 days
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        
        // Initialize table columns
        setupTableColumns();
        
        // Load initial data
        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null && endDate != null) {
            updateUsageStats(startDate, endDate);
            updateMaintenanceStats(startDate, endDate);
        }
    }

    private void updateUsageStats(LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Integer> usageStats = analyticsService.getEquipmentUsageStats(startDate, endDate);
            
            // Update bar chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Equipment Usage");
            
            List<EquipmentUsageStats> tableData = new ArrayList<>();
            
            for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
                String materialId = entry.getKey();
                int count = entry.getValue();
                
                // Add to chart
                series.getData().add(new XYChart.Data<>(materialId, count));
                
                // Add to table
                try {
                    Materiel materiel = materielService.getById(Integer.parseInt(materialId));
                    String type = materiel != null ? materiel.getType() : "Unknown";
                    String lastLoanDate = "N/A";
                    
                    // Get last loan date
                    List<Emprunt> loans = empruntService.getEmpruntsByMaterielId(Integer.parseInt(materialId));
                    if (!loans.isEmpty()) {
                        lastLoanDate = loans.get(0).getDateEmprunt().toString();
                    }
                    
                    tableData.add(new EquipmentUsageStats(materialId, type, count, lastLoanDate));
                } catch (SQLException e) {
                    System.err.println("Error getting material details: " + e.getMessage());
                }
            }
            
            usageChart.getData().clear();
            usageChart.getData().add(series);
            usageTable.setItems(FXCollections.observableArrayList(tableData));
            
        } catch (Exception e) {
            showAlert("Error", "Error updating usage statistics: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateMaintenanceStats(LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Integer> maintenanceStats = analyticsService.getMaintenanceStats(startDate, endDate);
            
            // Update pie chart
            maintenanceChart.getData().clear();
            List<MaintenanceStats> tableData = new ArrayList<>();
            
            for (Map.Entry<String, Integer> entry : maintenanceStats.entrySet()) {
                String materialId = entry.getKey();
                int count = entry.getValue();
                
                // Add to pie chart
                maintenanceChart.getData().add(new PieChart.Data("Equipment " + materialId, count));
                
                try {
                    Materiel materiel = materielService.getById(Integer.parseInt(materialId));
                    String lastMaintenanceDate = "N/A";
                    String status = "N/A";
                    
                    // Get last maintenance
                    List<Maintenance> maintenances = maintenanceService.getMaintenancesByMaterielId(Integer.parseInt(materialId));
                    if (!maintenances.isEmpty()) {
                        Maintenance lastMaintenance = maintenances.get(0);
                        lastMaintenanceDate = lastMaintenance.getDateMaintenance().toString();
                        status = lastMaintenance.getStatutMaintenance().toString();
                    }
                    
                    tableData.add(new MaintenanceStats(materialId, count, lastMaintenanceDate, status));
                } catch (SQLException e) {
                    System.err.println("Error getting material details: " + e.getMessage());
                }
            }
            
            maintenanceTable.setItems(FXCollections.observableArrayList(tableData));
            
        } catch (Exception e) {
            showAlert("Error", "Error updating maintenance statistics: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {
        equipmentIdColumn.setCellValueFactory(cellData -> cellData.getValue().equipmentIdProperty());
        equipmentTypeColumn.setCellValueFactory(cellData -> cellData.getValue().equipmentTypeProperty());
        loanCountColumn.setCellValueFactory(cellData -> cellData.getValue().loanCountProperty().asObject());
        lastLoanColumn.setCellValueFactory(cellData -> cellData.getValue().lastLoanDateProperty());
            
        maintEquipmentIdColumn.setCellValueFactory(cellData -> cellData.getValue().equipmentIdProperty());
        maintenanceCountColumn.setCellValueFactory(cellData -> cellData.getValue().maintenanceCountProperty().asObject());
        lastMaintenanceColumn.setCellValueFactory(cellData -> cellData.getValue().lastMaintenanceDateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    @FXML
    private void handleExport() {
        // TODO: Implement export functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export");
        alert.setHeaderText(null);
        alert.setContentText("Export functionality will be implemented soon!");
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 