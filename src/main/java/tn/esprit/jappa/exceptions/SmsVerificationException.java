package tn.esprit.jappa.exceptions;

public class SmsVerificationException extends Exception {
    public SmsVerificationException(String message) {
        super(message);
    }

    public SmsVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
} 