package tn.esprit.pidev.interfaces;

import tn.esprit.pidev.models.Evenement;
import java.util.List;

public interface IEventService {
    boolean addEvent(Evenement event);
    boolean updateEvent(Evenement event);
    boolean deleteEvent(int eventId);
    Evenement getEventById(int eventId);
    List<Evenement> getAllEvents();
    List<Evenement> searchEvents(String keyword);
} 