import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final String FILE_NAME = "users.txt";
    private List<User> users;

    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // no users yet
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Format: username,password,highScore
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    int highScore = 0;
                    try {
                        highScore = Integer.parseInt(parts[2].trim());
                    } catch (NumberFormatException e) {
                        highScore = 0;
                    }
                    users.add(new User(username, password, highScore));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User user : users) {
                bw.write(user.getUsername() + "," + user.getPassword() + "," + user.getHighScore());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing users file: " + e.getMessage());
        }
    }

    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User register(String username, String password) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        // check if username exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                throw new IllegalArgumentException("Username already exists.");
            }
        }
        User newUser = new User(username, password, 0);
        users.add(newUser);
        saveUsers();
        return newUser;
    }

    public void updateHighScore(User user, int newScore) {
        if (user == null) return;
        if (newScore > user.getHighScore()) {
            user.setHighScore(newScore);
            saveUsers();
        }
    }
}