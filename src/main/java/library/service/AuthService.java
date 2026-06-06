package library.service;

import library.model.User;
import library.model.User.Role;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    public AuthService() {
        // Default credentials
        users.put("admin", new User("admin", "admin123", Role.ADMINISTRATOR));
        users.put("librarian", new User("librarian", "lib123", Role.LIBRARIAN));
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
