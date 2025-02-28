package tn.esprit.pidev.interfaces;

import tn.esprit.pidev.models.Event;
import java.util.List;

public interface IEventService {
    boolean addEvent(Event event);
    boolean updateEvent(Event event);
    boolean deleteEvent(int eventId);
    Event getEventById(int eventId);
    List<Event> getAllEvents();
    List<Event> searchEvents(String keyword);
} 