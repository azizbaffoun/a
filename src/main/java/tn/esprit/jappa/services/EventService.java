package tn.esprit.jappa.services;

import tn.esprit.jappa.models.Event;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private Connection connection;

    public EventService() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public EventService(Connection connection) {
        this.connection = connection;
    }

    public List<Event> getAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Event event = new Event(
                    rs.getInt("ID"),
                    rs.getString("nom"),
                    rs.getString("details"),
                    rs.getDate("dateDebut").toLocalDate(),
                    rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                    rs.getString("type"),
                    rs.getInt("participantsMax")
                );
                events.add(event);
            }
        }
        return events;
    }

    public Event getById(int id) throws SQLException {
        String query = "SELECT * FROM evenement WHERE ID = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Event(
                    rs.getInt("ID"),
                    rs.getString("nom"),
                    rs.getString("details"),
                    rs.getDate("dateDebut").toLocalDate(),
                    rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                    rs.getString("type"),
                    rs.getInt("participantsMax")
                );
            }
        }
        return null;
    }
} 