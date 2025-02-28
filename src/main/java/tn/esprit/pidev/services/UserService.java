package tn.esprit.pidev.services;

import tn.esprit.pidev.interfaces.IUserService;
import tn.esprit.pidev.models.User;
import tn.esprit.pidev.utils.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {
    private Connection connection;

    public UserService() {
        connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean addUser(User user) {
        String query = "INSERT INTO utilisateur (email, motdepasse, genre, prenom, nom, " +
                      "numeroTelephone, adresse, photoProfil, roleID, nomOrganisation) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //hatinehelou ka query
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, user.getEmail());
            pst.setString(2, hashPassword(user.getMotdepasse()));
            pst.setString(3, user.getGenre());
            pst.setString(4, user.getPrenom());
            pst.setString(5, user.getNom());
            pst.setString(6, user.getNumeroTelephone());
            pst.setString(7, user.getAdresse());
            pst.setString(8, user.getPhotoProfil());
            pst.setInt(9, user.getRoleID());
            pst.setString(10, user.getNomOrganisation());// naamrou fel query bel index
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String query = "UPDATE utilisateur SET email=?, genre=?, prenom=?, nom=?, " +
                      "numeroTelephone=?, adresse=?, photoProfil=?, roleID=?, " +
                      "nomOrganisation=? WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getGenre());
            pst.setString(3, user.getPrenom());
            pst.setString(4, user.getNom());
            pst.setString(5, user.getNumeroTelephone());
            pst.setString(6, user.getAdresse());
            pst.setString(7, user.getPhotoProfil());
            pst.setInt(8, user.getRoleID());
            pst.setString(9, user.getNomOrganisation());
            pst.setInt(10, user.getID());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userID) {
        String query = "DELETE FROM utilisateur WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userID);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getUserById(int userID) {
        String query = "SELECT * FROM utilisateur WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userID);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM utilisateur WHERE email=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE email LIKE ? OR prenom LIKE ? OR " +
                      "nom LIKE ? OR nomOrganisation LIKE ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);
            pst.setString(4, searchPattern);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean resetPassword(int userID, String newPassword) {
        String query = "UPDATE utilisateur SET motdepasse=? WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, hashPassword(newPassword));
            pst.setInt(2, userID);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        String query = "SELECT motdepasse FROM utilisateur WHERE email=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("motdepasse");
                return storedHash.equals(hashPassword(password));
            }
        } catch (SQLException e) {
            System.err.println("Error validating credentials: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<User> getUsersByRole(int roleID) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE roleID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, roleID);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users by role: " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean updateProfilePhoto(int userID, String photoPath) {
        String query = "UPDATE utilisateur SET photoProfil=? WHERE ID=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, photoPath);
            pst.setInt(2, userID);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile photo: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setID(rs.getInt("ID"));
        user.setEmail(rs.getString("email"));
        user.setGenre(rs.getString("genre"));
        user.setPrenom(rs.getString("prenom"));
        user.setNom(rs.getString("nom"));
        user.setNumeroTelephone(rs.getString("numeroTelephone"));
        user.setAdresse(rs.getString("adresse"));
        user.setPhotoProfil(rs.getString("photoProfil"));
        user.setRoleID(rs.getInt("roleID"));
        user.setNomOrganisation(rs.getString("nomOrganisation"));
        return user;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
} 