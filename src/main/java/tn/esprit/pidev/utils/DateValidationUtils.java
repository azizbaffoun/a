package tn.esprit.pidev.utils;

import java.time.LocalDateTime;

public class DateValidationUtils {
    public static boolean isValidFutureDate(LocalDateTime date) {
        return date != null && date.isAfter(LocalDateTime.now());
    }
    
    public static boolean isValidDateRange(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null && 
               start.isAfter(LocalDateTime.now()) && 
               end.isAfter(start);
    }
    
    public static boolean isValidPastDate(LocalDateTime date) {
        return date != null && date.isBefore(LocalDateTime.now());
    }
    
    public static boolean isValidCurrentOrFutureDate(LocalDateTime date) {
        return date != null && !date.isBefore(LocalDateTime.now());
    }
    
    public static boolean isWithinRange(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return date != null && start != null && end != null &&
               !date.isBefore(start) && !date.isAfter(end);
    }
} 