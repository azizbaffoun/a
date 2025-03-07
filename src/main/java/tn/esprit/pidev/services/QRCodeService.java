package tn.esprit.pidev.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import tn.esprit.pidev.models.Emprunt;
import tn.esprit.pidev.models.Maintenance;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QRCodeService {
    private static final int QR_CODE_SIZE = 300;
    private final EmpruntService empruntService;
    private final MaintenanceService maintenanceService;
    private final String qrCodeDirectory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public QRCodeService() {
        this.empruntService = new EmpruntService();
        this.maintenanceService = new MaintenanceService();
        this.qrCodeDirectory = "qrcodes";
        createQRCodeDirectory();
    }

    private void createQRCodeDirectory() {
        try {
            Path directory = Paths.get(qrCodeDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }
        } catch (IOException e) {
            System.err.println("Failed to create QR code directory: " + e.getMessage());
        }
    }

    public Image generateQRCode(int materielId) throws WriterException, IOException {
        String qrContent = generateQRContent(materielId);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();
        
        return new Image(new ByteArrayInputStream(qrCodeBytes));
    }

    public void saveQRCode(int materielId) throws WriterException, IOException {
        String qrContent = generateQRContent(materielId);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
        
        String filePath = qrCodeDirectory + "/materiel_" + materielId + ".png";
        Path path = Paths.get(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    private String generateQRContent(int materielId) {
        StringBuilder content = new StringBuilder();
        content.append("Material ID: ").append(materielId).append("\n\n");
        
        // Add loan history
        content.append("Loan History:\n");
        getLoanHistory(materielId).forEach(emprunt -> {
            content.append("- Date: ").append(emprunt.getDateEmprunt().format(DATE_FORMATTER))
                  .append(", Return: ").append(emprunt.getDateRetour().format(DATE_FORMATTER))
                  .append(", Status: ").append(emprunt.getStatutEmprunt())
                  .append("\n");
        });
        
        // Add maintenance history
        content.append("\nMaintenance History:\n");
        getMaintenanceHistory(materielId).forEach(maintenance -> {
            content.append("- Date: ").append(maintenance.getDateMaintenance().format(DATE_FORMATTER))
                  .append(", Status: ").append(maintenance.getStatutMaintenance())
                  .append("\n");
        });
        
        return content.toString();
    }

    public List<Emprunt> getLoanHistory(int materielId) {
        return empruntService.getEmpruntsByMaterielId(materielId);
    }

    public List<Maintenance> getMaintenanceHistory(int materielId) {
        return maintenanceService.getMaintenancesByMaterielId(materielId);
    }
} 