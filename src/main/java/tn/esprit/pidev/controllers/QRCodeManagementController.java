package tn.esprit.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.services.MaterielService;
import tn.esprit.pidev.services.QRCodeService;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class QRCodeManagementController {
    @FXML private ListView<Materiel> materielListView;
    @FXML private ImageView qrCodeImageView;
    @FXML private TextArea historyTextArea;

    private final MaterielService materielService;
    private final QRCodeService qrCodeService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public QRCodeManagementController() {
        this.materielService = new MaterielService();
        this.qrCodeService = new QRCodeService();
    }

    @FXML
    private void initialize() {
        setupListView();
        loadMaterials();
    }

    private void setupListView() {
        materielListView.setCellFactory(param -> new ListCell<Materiel>() {
            @Override
            protected void updateItem(Materiel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (ID: %d)", 
                        item.getType(), item.getTypeSport(), item.getId()));
                }
            }
        });

        materielListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                generateQRCodeForMaterial(newVal);
            }
        });
    }

    private void loadMaterials() {
        try {
            materielListView.setItems(FXCollections.observableArrayList(materielService.getAll()));
        } catch (SQLException e) {
            showAlert("Error", "Error loading materials: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerateQRCode() {
        Materiel selected = materielListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a material first!", Alert.AlertType.ERROR);
            return;
        }
        generateQRCodeForMaterial(selected);
    }

    @FXML
    private void handleSaveAllQRCodes() {
        try {
            int count = 0;
            for (Materiel materiel : materielListView.getItems()) {
                try {
                    qrCodeService.saveQRCode(materiel.getId());
                    count++;
                } catch (Exception e) {
                    System.err.println("Error saving QR code for material " + materiel.getId() + ": " + e.getMessage());
                }
            }
            showAlert("Success", String.format("Successfully saved %d QR codes!", count), Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Error saving QR codes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void generateQRCodeForMaterial(Materiel materiel) {
        try {
            qrCodeImageView.setImage(qrCodeService.generateQRCode(materiel.getId()));
            displayHistory(materiel.getId());
        } catch (Exception e) {
            showAlert("Error", "Error generating QR code: " + e.getMessage(), Alert.AlertType.ERROR);
            qrCodeImageView.setImage(null);
            historyTextArea.clear();
        }
    }

    private void displayHistory(int materielId) {
        try {
            StringBuilder history = new StringBuilder();
            history.append("Material ID: ").append(materielId).append("\n\n");
            
            history.append("Loan History:\n");
            qrCodeService.getLoanHistory(materielId).forEach(emprunt -> {
                history.append("- Date: ").append(emprunt.getDateEmprunt().format(DATE_FORMATTER))
                      .append(", Return: ").append(emprunt.getDateRetour().format(DATE_FORMATTER))
                      .append(", Status: ").append(emprunt.getStatutEmprunt())
                      .append("\n");
            });
            
            history.append("\nMaintenance History:\n");
            qrCodeService.getMaintenanceHistory(materielId).forEach(maintenance -> {
                history.append("- Date: ").append(maintenance.getDateMaintenance().format(DATE_FORMATTER))
                      .append(", Status: ").append(maintenance.getStatutMaintenance())
                      .append("\n");
            });
            
            historyTextArea.setText(history.toString());
        } catch (Exception e) {
            historyTextArea.setText("Error loading history: " + e.getMessage());
        }
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