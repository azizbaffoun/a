package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import tn.esprit.pidev.models.Maintenance;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.services.MaintenanceService;
import tn.esprit.pidev.services.MaterielService;

import java.sql.SQLException;

public class MaintenanceDeleteController {
    private final MaintenanceService service;
    private final MaterielService materielService;

    @FXML private TableView<Maintenance> maintenanceTableView;
    @FXML private TableColumn<Maintenance, String> materielColumn;
    @FXML private TableColumn<Maintenance, String> dateMaintenanceColumn;
    @FXML private TableColumn<Maintenance, String> descriptionColumn;
    @FXML private TableColumn<Maintenance, Double> coutColumn;
    @FXML private TableColumn<Maintenance, String> statutColumn;
    @FXML private TableColumn<Maintenance, Void> actionColumn;
    @FXML private Label listHeader;

    public MaintenanceDeleteController() {
        this.service = new MaintenanceService();
        this.materielService = new MaterielService();
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        refreshTable();
    }

    private void setupTableColumns() {
        materielColumn.setCellValueFactory(cellData -> {
            try {
                Materiel materiel = materielService.getById(cellData.getValue().getMaterielID());
                return javafx.beans.binding.Bindings.createStringBinding(
                    () -> materiel.getType() + " - " + materiel.getTypeSport()
                );
            } catch (SQLException ex) {
                return javafx.beans.binding.Bindings.createStringBinding(
                    () -> "Error loading material"
                );
            }
        });
        
        dateMaintenanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateMaintenance"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        coutColumn.setCellValueFactory(new PropertyValueFactory<>("cout"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statutMaintenance"));
        
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.getStyleClass().add("button-danger");
                deleteButton.setOnAction(event -> {
                    Maintenance maintenance = getTableView().getItems().get(getIndex());
                    handleDelete(maintenance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(deleteButton);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
    }

    private void handleDelete(Maintenance maintenance) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this maintenance record?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.delete(maintenance.getMaintenanceID());
                refreshTable();
                showAlert("Success", "Maintenance record deleted successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Error", "Error deleting maintenance record: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void refreshTable() {
        try {
            maintenanceTableView.setItems(FXCollections.observableArrayList(service.getAll()));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading maintenance list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 