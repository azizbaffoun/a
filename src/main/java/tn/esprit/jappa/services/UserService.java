package tn.esprit.jappa.services;

import tn.esprit.jappa.models.User;
import tn.esprit.jappa.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private Connection connection;

    public UserService() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM chleghem.utilisateur";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("ID"));
                user.setPrenom(rs.getString("prenom"));
                user.setNom(rs.getString("nom"));
                users.add(user);
            }
        }
        return users;
    }
} 