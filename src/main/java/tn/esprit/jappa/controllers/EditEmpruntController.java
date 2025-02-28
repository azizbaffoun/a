package tn.esprit.jappa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.jappa.models.Emprunt;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.models.User;
import tn.esprit.jappa.models.EmpruntStatus;
import tn.esprit.jappa.services.EmpruntService;
import tn.esprit.jappa.services.MaterielService;
import tn.esprit.jappa.services.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EditEmpruntController {
    private final EmpruntService service;
    private final UserService userService;
    private final MaterielService materielService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private ListView<Emprunt> empruntListView;
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Materiel> materielComboBox;
    @FXML private DatePicker dateEmpruntPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private ComboBox<EmpruntStatus> statutEmpruntComboBox;
    @FXML private Label listHeader;

    public EditEmpruntController() {
        this.service = new EmpruntService();
        this.userService = new UserService();
        this.materielService = new MaterielService();
    }

    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        statutEmpruntComboBox.setItems(FXCollections.observableArrayList(EmpruntStatus.values()));
        refreshUserList();
        refreshMaterielList();
        setupListView();

        // Add tooltips
        userComboBox.setTooltip(new Tooltip("Select the user"));
        materielComboBox.setTooltip(new Tooltip("Select the material to loan"));
        dateEmpruntPicker.setTooltip(new Tooltip("Select the loan date"));
        dateRetourPicker.setTooltip(new Tooltip("Select the return date"));
        statutEmpruntComboBox.setTooltip(new Tooltip("Select the loan status"));
    }

    private void refreshUserList() {
        try {
            List<User> users = userService.getAll();
            userComboBox.setItems(FXCollections.observableArrayList(users));
            userComboBox.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            });
            userComboBox.setButtonCell(userComboBox.getCellFactory().call(null));
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
        try {
            List<Emprunt> emprunts = service.getAll();
            empruntListView.setItems(FXCollections.observableArrayList(emprunts));
            empruntListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    fillFields(newVal);
                }
            });
        } catch (SQLException ex) {
            showAlert("Error", "Error loading loan list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        Emprunt selected = empruntListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a loan to update", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInput()) {
            return;
        }

        updateEmprunt(selected);
    }

    private void updateEmprunt(Emprunt selected) {
        try {
            selected.setUserID(userComboBox.getValue().getId());
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
    private void handleClear() {
        clearFields();
    }

    private void refreshList() {
        try {
            List<Emprunt> emprunts = service.getAll();
            empruntListView.setItems(FXCollections.observableArrayList(emprunts));
        } catch (SQLException ex) {
            showAlert("Error", "Error refreshing loan list: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void fillFields(Emprunt emprunt) {
        try {
            User user = userService.getById(emprunt.getUserID());
            Materiel materiel = materielService.getById(emprunt.getMaterielID());
            userComboBox.setValue(user);
            materielComboBox.setValue(materiel);
            dateEmpruntPicker.setValue(emprunt.getDateEmprunt());
            dateRetourPicker.setValue(emprunt.getDateRetour());
            statutEmpruntComboBox.setValue(emprunt.getStatutEmprunt());
        } catch (SQLException ex) {
            showAlert("Error", "Error loading loan details: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (userComboBox.getValue() == null) {
            errorMessage.append("User is required\n");
        }
        if (materielComboBox.getValue() == null) {
            errorMessage.append("Material is required\n");
        }
        if (dateEmpruntPicker.getValue() == null) {
            errorMessage.append("Loan date is required\n");
        }
        if (dateRetourPicker.getValue() == null) {
            errorMessage.append("Return date is required\n");
        }
        if (statutEmpruntComboBox.getValue() == null) {
            errorMessage.append("Status is required\n");
        }

        // Additional date validations
        LocalDate now = LocalDate.now();
        LocalDate loanDate = dateEmpruntPicker.getValue();
        LocalDate returnDate = dateRetourPicker.getValue();

        if (loanDate != null && loanDate.isBefore(now)) {
            errorMessage.append("Loan date cannot be in the past\n");
        }
        if (loanDate != null && returnDate != null && !returnDate.isAfter(loanDate)) {
            errorMessage.append("Return date must be after loan date\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void clearFields() {
        userComboBox.setValue(null);
        materielComboBox.setValue(null);
        dateEmpruntPicker.setValue(null);
        dateRetourPicker.setValue(null);
        statutEmpruntComboBox.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 