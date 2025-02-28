package tn.esprit.pidev.controllers;

import tn.esprit.pidev.models.Venue;
import tn.esprit.pidev.services.VenueService;

import java.util.Date;
import java.util.List;

public class VenueController {
    private final VenueService venueService;

    public VenueController() {
        this.venueService = new VenueService();
    }

    public boolean createVenue(Venue venue) {
        return venueService.addVenue(venue);
    }

    public boolean updateVenue(Venue venue) {
        return venueService.updateVenue(venue);
    }

    public boolean deleteVenue(int venueId) {
        return venueService.deleteVenue(venueId);
    }

    public Venue getVenue(int venueId) {
        return venueService.getVenueById(venueId);
    }

    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    public List<Venue> getAvailableVenues() {
        return venueService.getAvailableVenues();
    }

    public boolean checkVenueAvailability(int venueId, Date date) {
        return venueService.checkVenueAvailability(venueId, date);
    }

    public boolean updateVenueStatus(int venueId, String status) {
        return venueService.updateVenueStatus(venueId, status);
    }

    public boolean scheduleVenueMaintenance(int venueId, Date startDate, Date endDate) {
        return venueService.scheduleVenueMaintenance(venueId, startDate, endDate);
    }
} 