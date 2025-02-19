package tn.esprit.jappa.services;

import tn.esprit.jappa.interfaces.IService;
import tn.esprit.jappa.models.Emprunt;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpruntService implements IService<Emprunt> {
    private Connection connection;

    public EmpruntService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void add(Emprunt emprunt) throws SQLException {
        String query = "INSERT INTO emprunt (userID, materielID, dateEmprunt, dateRetour, statutEmprunt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, emprunt.getUserID());
            pst.setInt(2, emprunt.getMaterielID());
            pst.setString(3, emprunt.getDateEmprunt());
            pst.setString(4, emprunt.getDateRetour());
            pst.setString(5, emprunt.getStatutEmprunt());
            pst.executeUpdate();
        }
    }

    @Override
    public void update(Emprunt emprunt) throws SQLException {
        String query = "UPDATE emprunt SET userID=?, materielID=?, dateEmprunt=?, dateRetour=?, statutEmprunt=? WHERE empruntID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, emprunt.getUserID());
            pst.setInt(2, emprunt.getMaterielID());
            pst.setString(3, emprunt.getDateEmprunt());
            pst.setString(4, emprunt.getDateRetour());
            pst.setString(5, emprunt.getStatutEmprunt());
            pst.setInt(6, emprunt.getEmpruntID());
            pst.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM emprunt WHERE empruntID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    @Override
    public Emprunt getById(int id) throws SQLException {
        String query = "SELECT * FROM emprunt WHERE empruntID=?";
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

    private Emprunt extractEmpruntFromResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setEmpruntID(rs.getInt("empruntID"));
        emprunt.setUserID(rs.getInt("userID"));
        emprunt.setMaterielID(rs.getInt("materielID"));
        emprunt.setDateEmprunt(rs.getString("dateEmprunt"));
        emprunt.setDateRetour(rs.getString("dateRetour"));
        emprunt.setStatutEmprunt(rs.getString("statutEmprunt"));
        return emprunt;
    }
} 