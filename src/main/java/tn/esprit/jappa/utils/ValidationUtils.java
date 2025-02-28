package tn.esprit.jappa.utils;

import java.time.LocalDate;
import java.util.Optional;

public class ValidationUtils {
    private static ValidationUtils instance;
    
    private ValidationUtils() {}
    
    public static ValidationUtils getInstance() {
        if (instance == null) {
            instance = new ValidationUtils();
        }
        return instance;
    }
    
    public Optional<String> validateBilletInput(String eventID, LocalDate dateAchat, 
            String prix, String typeBillet, String statut, String quantite) {
        
        StringBuilder errors = new StringBuilder();
        
        // Validate Event ID
        try {
            int id = Integer.parseInt(eventID);
            if (id <= 0) {
                errors.append("Event ID must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Event ID must be a valid number\n");
        }
        
        // Validate Date
        if (dateAchat == null) {
            errors.append("Date is required\n");
        }
        
        // Validate Price
        try {
            double priceValue = Double.parseDouble(prix);
            if (priceValue < 0) {
                errors.append("Price must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Price must be a valid number\n");
        }
        
        // Validate Type
        if (typeBillet == null || typeBillet.trim().isEmpty()) {
            errors.append("Ticket type is required\n");
        }
        
        // Validate Status
        if (statut == null || statut.trim().isEmpty()) {
            errors.append("Status is required\n");
        }
        
        // Validate Quantity
        try {
            int quantityValue = Integer.parseInt(quantite);
            if (quantityValue <= 0) {
                errors.append("Quantity must be a positive number\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Quantity must be a valid number\n");
        }
        
        return errors.length() > 0 ? Optional.of(errors.toString()) : Optional.empty();
    }
    
    public Optional<String> validateReservationInput(String billetID, String nombreBillet) {
        StringBuilder errors = new StringBuilder();
        
        // Validate Billet ID
        if (billetID == null || billetID.trim().isEmpty()) {
            errors.append("Ticket selection is required\n");
        }
        
        // Validate Number of Tickets
        try {
            int number = Integer.parseInt(nombreBillet);
            if (number <= 0) {
                errors.append("Number of tickets must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Number of tickets must be a valid number\n");
        }
        
        return errors.length() > 0 ? Optional.of(errors.toString()) : Optional.empty();
    }
} 