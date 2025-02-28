package tn.esprit.pidev.interfaces;

import tn.esprit.pidev.models.Venue;
import java.util.List;
import java.util.Date;

public interface IVenueService {
    boolean addVenue(Venue venue);
    boolean updateVenue(Venue venue);
    boolean deleteVenue(int venueId);
    Venue getVenueById(int venueId);
    List<Venue> getAllVenues();
    List<Venue> getAvailableVenues();
    boolean checkVenueAvailability(int venueId, Date date);
    boolean updateVenueStatus(int venueId, String status);
    boolean scheduleVenueMaintenance(int venueId, Date startDate, Date endDate);
} 