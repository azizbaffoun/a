package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.models.MaterielStatus;
import tn.esprit.pidev.services.MaterielService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MaterielEditController {
    private final MaterielService service;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ListView<Materiel> materielListView;
    @FXML private TextField typeField;
    @FXML private TextField typeSportField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<MaterielStatus> statutComboBox;
    @FXML private ComboBox<String> ownerTypeComboBox;
    @FXML private Label listHeader;

    public MaterielEditController() {
        this.service = new MaterielService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutComboBox.setItems(FXCollections.observableArrayList(MaterielStatus.values()));
        ownerTypeComboBox.setItems(FXCollections.observableArrayList("Club", "Fédération", "Privé"));

        // Setup ListView
        setupListView();
        
        // Add tooltips
        typeField.setTooltip(new Tooltip("Enter the type of material"));
        typeSportField.setTooltip(new Tooltip("Enter the sport type"));
        prixField.setTooltip(new Tooltip("Enter the price"));
        dateReservationPicker.setTooltip(new Tooltip("Select the reservation date"));
        statutComboBox.setTooltip(new Tooltip("Select the material status"));
        ownerTypeComboBox.setTooltip(new Tooltip("Select the owner type"));

        // Load initial data
        refreshList();
    }

    private void setupListView() {
        materielListView.setCellFactory(param -> new ListCell<Materiel>() {
            @Override
            protected void updateItem(Materiel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (Price: %.2f)", 
                        item.getType(), item.getTypeSport(), item.getPrix()));
                }
            }
        });

        materielListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });
    }

    @FXML
    private void handleUpdate() {
        Materiel selected = materielListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a material to update!", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            selected.setType(typeField.getText());
            selected.setTypeSport(typeSportField.getText());
            selected.setPrix(Double.parseDouble(prixField.getText()));
            selected.setDateReservation(dateReservationPicker.getValue().format(DATE_FORMATTER));
            selected.setStatut(statutComboBox.getValue().getDisplayName());
            selected.setOwnerType(ownerTypeComboBox.getValue());

            service.update(selected);
            refreshList();
            showAlert("Success", "Material updated successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error updating material: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid price!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        materielListView.getSelectionModel().clearSelection();
    }

    private void refreshList() {
        try {
            materielListView.setItems(FXCollections.observableArrayList(service.getAll()));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading material list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void fillFields(Materiel materiel) {
        typeField.setText(materiel.getType());
        typeSportField.setText(materiel.getTypeSport());
        prixField.setText(String.valueOf(materiel.getPrix()));
        if (materiel.getDateReservation() != null && !materiel.getDateReservation().isEmpty()) {
            dateReservationPicker.setValue(LocalDate.parse(materiel.getDateReservation(), DATE_FORMATTER));
        }
        statutComboBox.setValue(MaterielStatus.valueOf(materiel.getStatut().toUpperCase().replace(" ", "_")));
        ownerTypeComboBox.setValue(materiel.getOwnerType());
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (typeField.getText().trim().isEmpty()) {
            errorMessage.append("Type is required\n");
        }
        if (typeSportField.getText().trim().isEmpty()) {
            errorMessage.append("Sport type is required\n");
        }
        if (prixField.getText().trim().isEmpty()) {
            errorMessage.append("Price is required\n");
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText().trim());
                if (prix <= 0) {
                    errorMessage.append("Price must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a valid number\n");
            }
        }
        if (dateReservationPicker.getValue() == null) {
            errorMessage.append("Reservation date is required\n");
        }
        if (statutComboBox.getValue() == null) {
            errorMessage.append("Status is required\n");
        }
        if (ownerTypeComboBox.getValue() == null) {
            errorMessage.append("Owner type is required\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void clearFields() {
        typeField.clear();
        typeSportField.clear();
        prixField.clear();
        dateReservationPicker.setValue(null);
        statutComboBox.setValue(null);
        ownerTypeComboBox.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 