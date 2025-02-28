package tn.esprit.pidev.utils;

import tn.esprit.pidev.interfaces.EntityValidator;
import tn.esprit.pidev.models.Billet;
import java.util.Optional;

public class BilletValidator implements EntityValidator<Billet> {
    @Override
    public Optional<String> validate(Billet billet) {
        StringBuilder errors = new StringBuilder();
        
        // Date validation
        if (!DateValidationUtils.isValidFutureDate(billet.getDateAchat())) {
            errors.append("Purchase date must be in the future\n");
        }
        
        // Price validation
        if (billet.getPrix() <= 0) {
            errors.append("Price must be positive\n");
        }
        
        // Quantity validation
        if (billet.getQuantite() <= 0) {
            errors.append("Quantity must be positive\n");
        }
        
        // Event ID validation
        if (billet.getEvenementId() == null || billet.getEvenementId() <= 0) {
            errors.append("Invalid event ID\n");
        }
        
        return errors.length() > 0 ? Optional.of(errors.toString()) : Optional.empty();
    }
} 