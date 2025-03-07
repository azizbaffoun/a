package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.pidev.models.Emprunt;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.models.EmpruntStatus;
import tn.esprit.pidev.models.User;
import tn.esprit.pidev.services.EmpruntService;
import tn.esprit.pidev.services.MaterielService;
import tn.esprit.pidev.services.UserService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmpruntController {
    private final EmpruntService service;
    private final MaterielService materielService;
    private final UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ComboBox<User> userIDComboBox;
    @FXML private ComboBox<Materiel> materielComboBox;
    @FXML private DatePicker dateEmpruntPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private ComboBox<EmpruntStatus> statutEmpruntComboBox;
    @FXML private ListView<Emprunt> empruntListView;
    @FXML private Label listHeader;

    public EmpruntController() {
        this.service = new EmpruntService();
        this.materielService = new MaterielService();
        this.userService = new UserService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        refreshUserList();
        statutEmpruntComboBox.setItems(FXCollections.observableArrayList(EmpruntStatus.values()));
        refreshMaterielList();

        // Setup ListView header
        String headerFormat = "%-8s %-20s %-20s %-15s %-15s %-15s";
        listHeader.setText(String.format(headerFormat,
                "ID", "Date Emprunt", "Date Retour", "Status", "User", "Material"));

        // Setup ListView
        setupListView();
        refreshList();

        // Add tooltips
        userIDComboBox.setTooltip(new Tooltip("Select the user who is borrowing"));
        materielComboBox.setTooltip(new Tooltip("Select the material to borrow"));
        dateEmpruntPicker.setTooltip(new Tooltip("Select the borrow date"));
        dateRetourPicker.setTooltip(new Tooltip("Select the return date"));
        statutEmpruntComboBox.setTooltip(new Tooltip("Select the borrowing status"));

        // Add date validation
        dateRetourPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateEmpruntPicker.getValue() != null) {
                if (newVal.isBefore(dateEmpruntPicker.getValue())) {
                    showAlert("Error", "Return date cannot be before borrow date!", Alert.AlertType.ERROR);
                    dateRetourPicker.setValue(oldVal);
                }
            }
        });

        // Create buttons with consistent width
        HBox buttonBox = new HBox(10);

        Button[] buttons = {
            new Button("Add New Loan"),
            new Button("Edit Loan"),
            new Button("Delete Loan"),
            new Button("Clear")
        };

        // Add event handlers
        buttons[0].setOnAction(e -> handleAdd());
        buttons[1].setOnAction(e -> handleUpdate());
        buttons[2].setOnAction(e -> handleDelete());
        buttons[3].setOnAction(e -> handleClear());

        buttonBox.getChildren().addAll(buttons);
    }

    private void refreshUserList() {
        try {
            List<User> users = userService.getAll();
            userIDComboBox.setItems(FXCollections.observableArrayList(users));
            userIDComboBox.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNom() + " " + item.getPrenom() + " (ID: " + item.getId() + ")");
                    }
                }
            });
            userIDComboBox.setButtonCell(userIDComboBox.getCellFactory().call(null));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading user list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshMaterielList() {
        try {
            List<Materiel> materiels = materielService.getAll();
            materielComboBox.setItems(FXCollections.observableArrayList(materiels));
            materielComboBox.setCellFactory(param -> new ListCell<Materiel>() {
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
            materielComboBox.setButtonCell(materielComboBox.getCellFactory().call(null));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading material list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupListView() {
        empruntListView.setCellFactory(param -> new ListCell<Emprunt>() {
            @Override
            protected void updateItem(Emprunt item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: " + (getIndex() % 2 == 0 ? "#f5f5f5" : "#ffffff") + ";");
                }
            }
        });

        empruntListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Emprunt emprunt = new Emprunt();
            emprunt.setUserID(userIDComboBox.getValue().getId());
            emprunt.setMaterielID(materielComboBox.getValue().getId());
            emprunt.setDateEmprunt(dateEmpruntPicker.getValue());
            emprunt.setDateRetour(dateRetourPicker.getValue());
            emprunt.setStatutEmprunt(statutEmpruntComboBox.getValue());

            service.add(emprunt);
            refreshList();
            clearFields();
            showAlert("Success", "Loan added successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error adding loan: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        Emprunt selected = empruntListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a loan to update!", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInput()) {
            return;
        }

        updateEmprunt(selected);
    }

    private void updateEmprunt(Emprunt selected) {
        try {
            selected.setEmpruntID(userIDComboBox.getValue().getId());
            selected.setMaterielID(materielComboBox.getValue().getId());
            selected.setDateEmprunt(dateEmpruntPicker.getValue());
            selected.setDateRetour(dateRetourPicker.getValue());
            selected.setStatutEmprunt(statutEmpruntComboBox.getValue());

            service.update(selected);
            refreshList();
            showAlert("Success", "Loan updated successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error updating loan: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Emprunt selected = empruntListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a loan to delete!", Alert.AlertType.ERROR);
            return;
        }

        deleteEmprunt(selected);
    }

    private void deleteEmprunt(Emprunt selected) {
        try {
            service.delete(selected.getEmpruntID());
            refreshList();
            clearFields();
            showAlert("Success", "Loan deleted successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error", "Error deleting loan: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void refreshList() {
        try {
            empruntListView.setItems(FXCollections.observableArrayList(service.getAll()));
        } catch (SQLException ex) {
            showAlert("Error", "Error loading loan list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void fillFields(Emprunt emprunt) {
        userIDComboBox.getItems().stream()
            .filter(user -> user.getId() == emprunt.getUserID())
            .findFirst()
            .ifPresent(user -> userIDComboBox.setValue(user));
            
        try {
            Materiel materiel = materielService.getById(emprunt.getMaterielID());
            materielComboBox.setValue(materiel);
        } catch (SQLException ex) {
            showAlert("Error", "Error loading material: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        
        dateEmpruntPicker.setValue(emprunt.getDateEmprunt());
        dateRetourPicker.setValue(emprunt.getDateRetour());
        statutEmpruntComboBox.setValue(emprunt.getStatutEmprunt());
    }

    private void clearFields() {
        userIDComboBox.setValue(null);
        materielComboBox.setValue(null);
        dateEmpruntPicker.setValue(null);
        dateRetourPicker.setValue(null);
        statutEmpruntComboBox.setValue(null);
        empruntListView.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (userIDComboBox.getValue() == null) {
            errorMessage.append("User selection is required.\n");
        }
        if (materielComboBox.getValue() == null) {
            errorMessage.append("Material selection is required.\n");
        }
        if (dateEmpruntPicker.getValue() == null) {
            errorMessage.append("Borrow date is required.\n");
        }
        if (dateRetourPicker.getValue() == null) {
            errorMessage.append("Return date is required.\n");
        }
        if (statutEmpruntComboBox.getValue() == null) {
            errorMessage.append("Status is required.\n");
        }
        if (dateEmpruntPicker.getValue() != null && dateRetourPicker.getValue() != null) {
            if (dateRetourPicker.getValue().isBefore(dateEmpruntPicker.getValue())) {
                errorMessage.append("Return date cannot be before borrow date.\n");
            }
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