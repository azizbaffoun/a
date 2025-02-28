package tn.esprit.pidev.exceptions;

import tn.esprit.pidev.models.ApiResponse;
import java.util.Collections;

public class GlobalExceptionHandler {
    
    public <T> ApiResponse<T> handleValidationException(ValidationException ex) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("Validation failed");
        response.setErrors(ex.getErrors());
        return response;
    }
    
    public <T> ApiResponse<T> handleDateValidationException(DateValidationException ex) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("Date validation failed");
        response.setErrors(Collections.singletonList(ex.getMessage()));
        return response;
    }
    
    public <T> ApiResponse<T> handleException(Exception ex) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("An unexpected error occurred");
        response.setErrors(Collections.singletonList(ex.getMessage()));
        return response;
    }
} 