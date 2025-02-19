package tn.esprit.jappa.services;

import tn.esprit.jappa.interfaces.IService;
import tn.esprit.jappa.models.Materiel;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterielService implements IService<Materiel> {
    private Connection connection;

    public MaterielService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void add(Materiel materiel) throws SQLException {
        String query = "INSERT INTO materiel (type, typeSport, prix, dateReservation, statut, ownerType) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, materiel.getType());
            pst.setString(2, materiel.getTypeSport());
            pst.setDouble(3, materiel.getPrix());
            pst.setString(4, materiel.getDateReservation());
            pst.setString(5, materiel.getStatut());
            pst.setString(6, materiel.getOwnerType());
            pst.executeUpdate();
        }
    }

    @Override
    public void update(Materiel materiel) throws SQLException {
        String query = "UPDATE materiel SET type=?, typeSport=?, prix=?, dateReservation=?, statut=?, ownerType=? WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, materiel.getType());
            pst.setString(2, materiel.getTypeSport());
            pst.setDouble(3, materiel.getPrix());
            pst.setString(4, materiel.getDateReservation());
            pst.setString(5, materiel.getStatut());
            pst.setString(6, materiel.getOwnerType());
            pst.setInt(7, materiel.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM materiel WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    @Override
    public Materiel getById(int id) throws SQLException {
        String query = "SELECT * FROM materiel WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractMaterielFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Materiel> getAll() throws SQLException {
        List<Materiel> materiels = new ArrayList<>();
        String query = "SELECT * FROM materiel";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                materiels.add(extractMaterielFromResultSet(rs));
            }
        }
        return materiels;
    }

    private Materiel extractMaterielFromResultSet(ResultSet rs) throws SQLException {
        Materiel materiel = new Materiel();
        materiel.setId(rs.getInt("ID"));
        materiel.setType(rs.getString("type"));
        materiel.setTypeSport(rs.getString("typeSport"));
        materiel.setPrix(rs.getDouble("prix"));
        materiel.setDateReservation(rs.getString("dateReservation"));
        materiel.setStatut(rs.getString("statut"));
        materiel.setOwnerType(rs.getString("ownerType"));
        return materiel;
    }
} 