package tn.esprit.pidev.services;

import tn.esprit.pidev.models.User;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Connection connection;
    
    public UserService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT ID, prenom, nom FROM utilisateur";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("ID"),
                    rs.getString("prenom"),
                    rs.getString("nom")
                ));
            }
        }
        return users;
    }
    
    public User getById(int id) throws SQLException {
        String query = "SELECT ID, prenom, nom FROM utilisateur WHERE ID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("ID"),
                    rs.getString("prenom"),
                    rs.getString("nom")
                );
            }
        }
        return null;
    }
} 