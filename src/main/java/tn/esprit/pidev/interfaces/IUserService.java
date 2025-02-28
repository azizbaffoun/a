package tn.esprit.pidev.interfaces;

import tn.esprit.pidev.models.User;
import java.util.List;

public interface IUserService {
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int userID);
    User getUserById(int userID);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    List<User> searchUsers(String keyword);
    boolean resetPassword(int userID, String newPassword);
    boolean validateCredentials(String email, String password);
    List<User> getUsersByRole(int roleID);
    boolean updateProfilePhoto(int userID, String photoPath);
} 