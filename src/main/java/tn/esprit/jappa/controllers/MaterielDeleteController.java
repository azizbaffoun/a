package tn.esprit.jappa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.services.MaterielService;

import java.sql.SQLException;

public class MaterielDeleteController {
    private final MaterielService service;

    @FXML private TableView<Materiel> materielTableView;
    @FXML private TableColumn<Materiel, String> typeColumn;
    @FXML private TableColumn<Materiel, String> typeSportColumn;
    @FXML private TableColumn<Materiel, Double> prixColumn;
    @FXML private TableColumn<Materiel, String> dateReservationColumn;
    @FXML private TableColumn<Materiel, String> statutColumn;
    @FXML private TableColumn<Materiel, String> ownerTypeColumn;
    @FXML private TableColumn<Materiel, Void> actionColumn;
    @FXML private Label listHeader;

    public MaterielDeleteController() {
        this.service = new MaterielService();
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        refreshTable();
    }

    private void setupTableColumns() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeSportColumn.setCellValueFactory(new PropertyValueFactory<>("typeSport"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        dateReservationColumn.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        ownerTypeColumn.setCellValueFactory(new PropertyValueFactory<>("ownerType"));
        
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.getStyleClass().add("button-danger");
                deleteButton.setOnAction(event -> {
                    Materiel materiel = getTableView().getItems().get(getIndex());
                    handleDelete(materiel);
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

    private void handleDelete(Materiel materiel) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this material?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.delete(materiel.getId());
                refreshTable();
                showAlert("Success", "Material deleted successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Error", "Error deleting material: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void refreshTable() {
        try {
            materielTableView.setItems(FXCollections.observableArrayList(service.getAll()));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading material list: " + ex.getMessage(), Alert.AlertType.ERROR);
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