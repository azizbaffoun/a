package tn.esprit.pidev.exceptions;

import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errors;
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public ValidationException(String message) {
        this(message, Collections.singletonList(message));
    }
    
    public List<String> getErrors() {
        return errors;
    }
} 