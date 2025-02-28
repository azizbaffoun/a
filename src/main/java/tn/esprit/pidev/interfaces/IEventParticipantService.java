package tn.esprit.pidev.interfaces;

import tn.esprit.pidev.models.EventParticipant;
import java.util.List;

public interface IEventParticipantService {
    boolean addParticipant(EventParticipant participant);
    boolean removeParticipant(int userId, int eventId);
    boolean updateParticipantRole(EventParticipant participant);
    List<EventParticipant> getEventParticipants(int eventId);
    List<EventParticipant> getUserEvents(int userId);
} 