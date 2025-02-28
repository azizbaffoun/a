package tn.esprit.pidev.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPreferences {
    private Map<String, Double> eventTypePreferences;
    private Location preferredLocation;
    private Map<LocalDateTime, Integer> participationFrequency;
    private List<Integer> pastEventIds;

    public UserPreferences() {
        this.eventTypePreferences = new HashMap<>();
        this.participationFrequency = new HashMap<>();
    }

    // Getters and Setters
    public Map<String, Double> getEventTypePreferences() {
        return eventTypePreferences;
    }

    public void setEventTypePreferences(Map<String, Double> eventTypePreferences) {
        this.eventTypePreferences = eventTypePreferences;
    }

    public Location getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(Location preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public Map<LocalDateTime, Integer> getParticipationFrequency() {
        return participationFrequency;
    }

    public void setParticipationFrequency(Map<LocalDateTime, Integer> participationFrequency) {
        this.participationFrequency = participationFrequency;
    }

    public List<Integer> getPastEventIds() {
        return pastEventIds;
    }

    public void setPastEventIds(List<Integer> pastEventIds) {
        this.pastEventIds = pastEventIds;
    }

    public void addEventTypePreference(String eventType, Double weight) {
        this.eventTypePreferences.put(eventType, weight);
    }

    public void addParticipation(LocalDateTime date) {
        this.participationFrequency.merge(date, 1, Integer::sum);
    }
} 