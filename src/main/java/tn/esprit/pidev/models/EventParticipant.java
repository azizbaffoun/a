package tn.esprit.pidev.models;

public class EventParticipant {
    private int userID;
    private int eventID;
    private String userRoleInEvent;

    // Constructors
    public EventParticipant() {}

    public EventParticipant(int userID, int eventID, String userRoleInEvent) {
        this.userID = userID;
        this.eventID = eventID;
        this.userRoleInEvent = userRoleInEvent;
    }

    // Getters and Setters
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public String getUserRoleInEvent() { return userRoleInEvent; }
    public void setUserRoleInEvent(String userRoleInEvent) { this.userRoleInEvent = userRoleInEvent; }

    @Override
    public String toString() {
        return "EventParticipant{" +
                "userID=" + userID +
                ", eventID=" + eventID +
                ", userRoleInEvent='" + userRoleInEvent + '\'' +
                '}';
    }
} 