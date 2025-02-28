package tn.esprit.jappa.services;

import tn.esprit.jappa.interfaces.IService;
import tn.esprit.jappa.models.Emprunt;
import tn.esprit.jappa.models.EmpruntStatus;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntService implements IService<Emprunt> {
    private Connection connection;

    public EmpruntService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void add(Emprunt emprunt) throws SQLException {
        String query = "INSERT INTO emprunt (user_id, materiel_id, date_emprunt, date_retour, statut_emprunt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, emprunt.getUserID());
            ps.setInt(2, emprunt.getMaterielID());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(emprunt.getDateRetour()));
            ps.setString(5, emprunt.getStatutEmprunt().name());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Emprunt emprunt) throws SQLException {
        String query = "UPDATE emprunt SET user_id=?, materiel_id=?, date_emprunt=?, date_retour=?, statut_emprunt=? WHERE emprunt_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, emprunt.getUserID());
            ps.setInt(2, emprunt.getMaterielID());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(emprunt.getDateRetour()));
            ps.setString(5, emprunt.getStatutEmprunt().name());
            ps.setInt(6, emprunt.getEmpruntID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM emprunt WHERE emprunt_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Emprunt getById(int id) throws SQLException {
        String query = "SELECT * FROM emprunt WHERE emprunt_id=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractEmpruntFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Emprunt> getAll() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String query = "SELECT * FROM emprunt";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
        }
        return emprunts;
    }

    public boolean isDateTaken(int materielId, LocalDate date) throws SQLException {
        String query = "SELECT COUNT(*) FROM emprunt WHERE materiel_id = ? AND date_emprunt <= ? AND date_retour >= ? AND statut_emprunt != ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, materielId);
            ps.setDate(2, Date.valueOf(date));
            ps.setDate(3, Date.valueOf(date));
            ps.setString(4, EmpruntStatus.CANCELLED.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int getLoanCount(int materielId) throws SQLException {
        String query = "SELECT COUNT(*) FROM emprunt WHERE materiel_id = ? AND statut_emprunt != ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, materielId);
            ps.setString(2, EmpruntStatus.CANCELLED.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Emprunt extractEmpruntFromResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setEmpruntID(rs.getInt("emprunt_id"));
        emprunt.setUserID(rs.getInt("user_id"));
        emprunt.setMaterielID(rs.getInt("materiel_id"));
        emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        emprunt.setDateRetour(rs.getDate("date_retour").toLocalDate());
        emprunt.setStatutEmprunt(EmpruntStatus.valueOf(rs.getString("statut_emprunt")));
        return emprunt;
    }
} 