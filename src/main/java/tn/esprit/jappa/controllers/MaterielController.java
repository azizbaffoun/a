package tn.esprit.jappa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.models.MaterielStatus;
import tn.esprit.jappa.services.MaterielService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MaterielController {
    private final MaterielService service;
    
    @FXML private ListView<Materiel> materielListView;
    @FXML private TextField typeField;
    @FXML private TextField typeSportField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<MaterielStatus> statutComboBox;
    @FXML private ComboBox<String> ownerTypeComboBox;
    @FXML private Label listHeader;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MaterielController() {
        this.service = new MaterielService();
    }

    public VBox getView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setFillWidth(true);

        // Create form fields
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        // Make columns flexible
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(100);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labelColumn, fieldColumn);

        typeField = new TextField();
        typeField.setPromptText("Enter material type");
        typeField.setTooltip(new Tooltip("Enter the type of material (e.g., Ball, Racket)"));
        typeField.setMaxWidth(Double.MAX_VALUE);

        typeSportField = new TextField();
        typeSportField.setPromptText("Enter sport type");
        typeSportField.setTooltip(new Tooltip("Enter the type of sport (e.g., Football, Tennis)"));
        typeSportField.setMaxWidth(Double.MAX_VALUE);

        prixField = new TextField();
        prixField.setPromptText("Enter price");
        prixField.setTooltip(new Tooltip("Enter the price (numeric value only)"));
        prixField.setMaxWidth(Double.MAX_VALUE);

        dateReservationPicker = new DatePicker();
        dateReservationPicker.setPromptText("Select reservation date");
        dateReservationPicker.setTooltip(new Tooltip("Select the reservation date"));
        dateReservationPicker.setMaxWidth(Double.MAX_VALUE);

        statutComboBox = new ComboBox<>(FXCollections.observableArrayList(MaterielStatus.values()));
        statutComboBox.setPromptText("Select status");
        statutComboBox.setTooltip(new Tooltip("Select the current status of the material"));
        statutComboBox.setMaxWidth(Double.MAX_VALUE);

        ownerTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "Club", "Fédération", "Privé"
        ));
        ownerTypeComboBox.setPromptText("Select owner type");
        ownerTypeComboBox.setTooltip(new Tooltip("Select the type of owner"));
        ownerTypeComboBox.setMaxWidth(Double.MAX_VALUE);

        // Style labels
        Label[] labels = {
            new Label("Type:"),
            new Label("Type Sport:"),
            new Label("Prix:"),
            new Label("Date Reservation:"),
            new Label("Statut:"),
            new Label("Owner Type:")
        };
        
        for (Label label : labels) {
            label.setStyle("-fx-font-weight: bold;");
        }

        form.addRow(0, labels[0], typeField);
        form.addRow(1, labels[1], typeSportField);
        form.addRow(2, labels[2], prixField);
        form.addRow(3, labels[3], dateReservationPicker);
        form.addRow(4, labels[4], statutComboBox);
        form.addRow(5, labels[5], ownerTypeComboBox);

        // Create buttons with consistent width
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        buttonBox.setAlignment(Pos.CENTER);

        Button[] buttons = {
            new Button("Add New Material"),
            new Button("Edit Material"),
            new Button("Delete Material"),
            new Button("Clear")
        };

        // Style buttons
        for (Button button : buttons) {
            button.setMinWidth(100);
            button.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;");
            
            // Hover effect
            button.setOnMouseEntered(e -> 
                button.setStyle("-fx-background-color: #283593; -fx-text-fill: white;"));
            button.setOnMouseExited(e -> 
                button.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"));
        }

        buttonBox.getChildren().addAll(buttons);

        // Add event handlers
        buttons[0].setOnAction(e -> handleAdd());
        buttons[1].setOnAction(e -> handleUpdate());
        buttons[2].setOnAction(e -> handleDelete());
        buttons[3].setOnAction(e -> handleClear());

        // Create list view
        materielListView = new ListView<>();
        materielListView.setPrefHeight(400);
        VBox.setVgrow(materielListView, Priority.ALWAYS);
        refreshList();

        materielListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });

        vbox.getChildren().addAll(
            form,
            buttonBox,
            new Label("Materiel List:"),
            materielListView
        );
        
        return vbox;
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutComboBox.setItems(FXCollections.observableArrayList(MaterielStatus.values()));
        ownerTypeComboBox.setItems(FXCollections.observableArrayList("Club", "Fédération", "Privé"));
        
        // Setup ListView header
        String headerFormat = "%-8s %-20s %-20s %-15s %-15s %-15s";
        listHeader.setText(String.format(headerFormat,
                "ID", "Type", "Sport", "Prix", "Owner", "Status"));
        
        // Setup ListView styling
        setupListView();
        
        // Load initial data
        refreshList();
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Materiel materiel = new Materiel();
            materiel.setType(typeField.getText());
            materiel.setTypeSport(typeSportField.getText());
            materiel.setPrix(Double.parseDouble(prixField.getText()));
            materiel.setDateReservation(dateReservationPicker.getValue().format(DATE_FORMATTER));
            materiel.setStatut(statutComboBox.getValue().getDisplayName());
            materiel.setOwnerType(ownerTypeComboBox.getValue());

            service.add(materiel);
            refreshList();
            clearFields();
            showAlert("Success", "Materiel added successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error adding materiel: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid price!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        Materiel selected = materielListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a materiel to update!", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInput()) {
            return;
        }

        updateMateriel(selected);
    }

    private void updateMateriel(Materiel selected) {
        try {
            selected.setType(typeField.getText());
            selected.setTypeSport(typeSportField.getText());
            selected.setPrix(Double.parseDouble(prixField.getText()));
            selected.setDateReservation(dateReservationPicker.getValue().format(DATE_FORMATTER));
            selected.setStatut(statutComboBox.getValue().getDisplayName());
            selected.setOwnerType(ownerTypeComboBox.getValue());

            service.update(selected);
            refreshList();
            showAlert("Success", "Materiel updated successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error updating materiel: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid price!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Materiel selected = materielListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a materiel to delete!", Alert.AlertType.ERROR);
            return;
        }

        deleteMateriel(selected);
    }

    private void deleteMateriel(Materiel selected) {
        try {
            service.delete(selected.getId());
            refreshList();
            clearFields();
            showAlert("Success", "Materiel deleted successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error deleting materiel: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void setupListView() {
        materielListView.setCellFactory(param -> new ListCell<Materiel>() {
            @Override
            protected void updateItem(Materiel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        materielListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });
    }

    private void refreshList() {
        try {
            // Update ListView items
            materielListView.setItems(FXCollections.observableArrayList(service.getAll()));
            materielListView.setCellFactory(param -> new ListCell<Materiel>() {
                @Override
                protected void updateItem(Materiel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle(null);
                    } else {
                        setText(String.format("%-8d %-20s %-20s %-15.2f %-15s %-15s",
                                item.getId(),
                                item.getType(),
                                item.getTypeSport(),
                                item.getPrix(),
                                item.getOwnerType(),
                                item.getStatut()));
                                
                        // Modern styling for rows
                        setStyle("-fx-padding: 5 10 5 10; " +
                                "-fx-font-family: 'Monospace'; " +
                                "-fx-font-size: 14px; " +
                                "-fx-background-color: " + (getIndex() % 2 == 0 ? "#f5f5f5" : "#ffffff") + ";");
                    }
                    
                    // Hover effect
                    setOnMouseEntered(event -> setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: #e3f2fd;"));
                            
                    setOnMouseExited(event -> setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: " + (getIndex() % 2 == 0 ? "#f5f5f5" : "#ffffff") + ";"));
                }
            });

            // Style the ListView
            materielListView.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-color: #1a237e; " +
                    "-fx-border-width: 1;");

        } catch (SQLException ex) {
            showAlert("Error", "Error loading materiel list: " + ex.getMessage(), Alert.AlertType.ERROR);
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

    private void clearFields() {
        typeField.clear();
        typeSportField.clear();
        prixField.clear();
        dateReservationPicker.setValue(null);
        statutComboBox.setValue(null);
        ownerTypeComboBox.setValue(null);
        materielListView.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (typeField.getText().trim().isEmpty()) {
            errorMessage.append("Type is required.\n");
        }
        if (typeSportField.getText().trim().isEmpty()) {
            errorMessage.append("Sport type is required.\n");
        }
        if (prixField.getText().trim().isEmpty()) {
            errorMessage.append("Price is required.\n");
        } else {
            try {
                double price = Double.parseDouble(prixField.getText());
                if (price < 0) {
                    errorMessage.append("Price must be positive.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a valid number.\n");
            }
        }
        if (dateReservationPicker.getValue() == null) {
            errorMessage.append("Reservation date is required.\n");
        }
        if (statutComboBox.getValue() == null) {
            errorMessage.append("Status is required.\n");
        }
        if (ownerTypeComboBox.getValue() == null) {
            errorMessage.append("Owner type is required.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 