package tn.esprit.pidev.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {
    private static FirebaseApp firebaseApp;
    
    public static void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FileInputStream serviceAccount = new FileInputStream("firebase-credentials.json");
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                
                firebaseApp = FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
            } catch (IOException e) {
                System.err.println("Error initializing Firebase: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("Firebase already initialized");
        }
    }
    
    public static void logEvent(String eventName, String paramName, String paramValue) {
        // Note: This is a simplified logging implementation
        // Consider implementing a more robust logging system based on your needs
        if (isInitialized()) {
            System.out.println("Event logged: " + eventName + " - " + paramName + ": " + paramValue);
        } else {
            System.err.println("Firebase not initialized. Cannot log event.");
        }
    }
    
    public static boolean isInitialized() {
        return !FirebaseApp.getApps().isEmpty();
    }
} 