package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import tn.esprit.pidev.services.QRCodeService;
import tn.esprit.pidev.services.EmpruntService;
import tn.esprit.pidev.services.MaintenanceService;
import java.time.format.DateTimeFormatter;

public class QRCodeController {
    @FXML private TextField materielIdField;
    @FXML private ImageView qrCodeImageView;
    @FXML private TextArea historyTextArea;

    private final QRCodeService qrCodeService;
    private final EmpruntService empruntService;
    private final MaintenanceService maintenanceService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public QRCodeController() {
        this.qrCodeService = new QRCodeService();
        this.empruntService = new EmpruntService();
        this.maintenanceService = new MaintenanceService();
    }

    public void setMaterielId(int materielId) {
        materielIdField.setText(String.valueOf(materielId));
        handleGenerateQRCode();
    }

    @FXML
    private void handleGenerateQRCode() {
        try {
            int materielId = Integer.parseInt(materielIdField.getText().trim());
            qrCodeImageView.setImage(qrCodeService.generateQRCode(materielId));
            displayHistory(materielId);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid Material ID", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Error generating QR code: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSaveQRCode() {
        try {
            int materielId = Integer.parseInt(materielIdField.getText().trim());
            qrCodeService.saveQRCode(materielId);
            showAlert("Success", "QR code saved successfully!", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid Material ID", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Error saving QR code: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayHistory(int materielId) {
        StringBuilder history = new StringBuilder();
        history.append("Material ID: ").append(materielId).append("\n\n");
        
        history.append("Loan History:\n");
        empruntService.getEmpruntsByMaterielId(materielId).forEach(emprunt -> {
            history.append("- Date: ").append(emprunt.getDateEmprunt().format(DATE_FORMATTER))
                  .append(", Return: ").append(emprunt.getDateRetour().format(DATE_FORMATTER))
                  .append(", Status: ").append(emprunt.getStatutEmprunt())
                  .append("\n");
        });
        
        history.append("\nMaintenance History:\n");
        maintenanceService.getMaintenancesByMaterielId(materielId).forEach(maintenance -> {
            history.append("- Date: ").append(maintenance.getDateMaintenance().format(DATE_FORMATTER))
                  .append(", Status: ").append(maintenance.getStatutMaintenance())
                  .append("\n");
        });
        
        historyTextArea.setText(history.toString());
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