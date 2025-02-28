package tn.esprit.pidev.controllers;

import tn.esprit.pidev.models.EventParticipant;
import tn.esprit.pidev.services.EventParticipantService;

import java.util.List;

public class EventParticipantController {
    private final EventParticipantService participantService;

    public EventParticipantController() {
        this.participantService = new EventParticipantService();
    }

    public boolean addParticipant(EventParticipant participant) {
        return participantService.addParticipant(participant);
    }

    public boolean removeParticipant(int userId, int eventId) {
        return participantService.removeParticipant(userId, eventId);
    }

    public boolean updateParticipantRole(EventParticipant participant) {
        return participantService.updateParticipantRole(participant);
    }

    public List<EventParticipant> getEventParticipants(int eventId) {
        return participantService.getEventParticipants(eventId);
    }

    public List<EventParticipant> getUserEvents(int userId) {
        return participantService.getUserEvents(userId);
    }
} 