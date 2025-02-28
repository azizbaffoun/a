package tn.esprit.pidev.services;

import tn.esprit.pidev.models.UserPreferences;
import tn.esprit.pidev.models.Evenement;
import tn.esprit.pidev.models.Location;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventRecommendationEngine {
    private static final double LOCATION_WEIGHT = 0.3;
    private static final double EVENT_TYPE_WEIGHT = 0.4;
    private static final double PARTICIPATION_WEIGHT = 0.3;
    
    private final PreferenceAnalyzer preferenceAnalyzer;
    
    public EventRecommendationEngine(PreferenceAnalyzer preferenceAnalyzer) {
        this.preferenceAnalyzer = preferenceAnalyzer;
    }
    
    public List<Evenement> getRecommendations(int userId) {
        UserPreferences preferences = preferenceAnalyzer.analyzePreferences(userId);
        return calculateRecommendations(preferences);
    }
    
    private List<Evenement> calculateRecommendations(UserPreferences preferences) {
        // Get all available events (this should be injected or fetched from a service)
        List<Evenement> availableEvents = getAllAvailableEvents();
        
        // Calculate scores for each event
        Map<Evenement, Double> eventScores = availableEvents.stream()
            .collect(Collectors.toMap(
                event -> event,
                event -> calculateEventScore(event, preferences)
            ));
        
        // Sort by score and return top events
        return eventScores.entrySet().stream()
            .sorted(Map.Entry.<Evenement, Double>comparingByValue().reversed())
            .limit(10) // Return top 10 recommendations
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    private double calculateEventScore(Evenement event, UserPreferences prefs) {
        double typeScore = calculateTypeMatchScore(event, prefs);
        double locationScore = calculateLocationScore(event, prefs);
        double timeScore = calculateTimePreferenceScore(event, prefs);
        
        return (typeScore * EVENT_TYPE_WEIGHT) +
               (locationScore * LOCATION_WEIGHT) +
               (timeScore * PARTICIPATION_WEIGHT);
    }
    
    private double calculateTypeMatchScore(Evenement event, UserPreferences prefs) {
        return prefs.getEventTypePreferences().getOrDefault(event.getType(), 0.0);
    }
    
    private double calculateLocationScore(Evenement event, UserPreferences prefs) {
        if (prefs.getPreferredLocation() == null || event.getLocation() == null) {
            return 0.0;
        }
        
        double distance = prefs.getPreferredLocation().distanceTo(event.getLocation());
        // Convert distance to a score between 0 and 1 (closer = higher score)
        return Math.max(0, 1 - (distance / 100)); // Assuming 100km as max relevant distance
    }
    
    private double calculateTimePreferenceScore(Evenement event, UserPreferences prefs) {
        // Implementation depends on how you want to score time preferences
        // This is a simple example that could be enhanced
        return prefs.getParticipationFrequency().containsKey(event.getDateDebut()) ? 1.0 : 0.5;
    }
    
    // This method should be replaced with actual data access
    private List<Evenement> getAllAvailableEvents() {
        throw new UnsupportedOperationException("This method should be implemented with actual data access");
    }
} 