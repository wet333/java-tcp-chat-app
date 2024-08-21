package online.awet.system.userManagement;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class AccountsManager {

    private static final String accountsStorageFile = "accounts.txt";
    private static AccountsManager instance;

    private Set<User> users;

    private AccountsManager() {
        try {
            this.users = this.loadAccountList();

        } catch (IOException e) {
            System.out.println("I/O exception on File: " + accountsStorageFile);
        }
    }

    public static AccountsManager getInstance() {
        if (instance == null) {
            instance = new AccountsManager();
        }
        return instance;
    }

    public void addAccount(String username, String password) {
        users.add(new User(username, password));
        try {
            saveAccountList();
        } catch (IOException e) {
            System.out.println("There was an error saving the user: " + username + ", couldn't open file: " + accountsStorageFile);
        }
    }

    public void deleteAccount(String username, String password) {
        users.removeIf(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }

    public User findAccount(String username, String password) {
        User targetUser = null;
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                targetUser = user;
                break;
            }
        }
        return targetUser;
    }

    // Save all users in the format "username:password;"
    public void saveAccountList() throws IOException {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(accountsStorageFile));
            for (User user : this.users) {
                fileWriter.write(user.getUsername() + ":" + user.getPassword() + ";" + System.lineSeparator());
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error saving users, File not found: " + accountsStorageFile);
        }
    }

    // Reads all user's credentials from accountsStorageFile file and loads them into memory
    // The user list is handled in memory not in the file
    public Set<User> loadAccountList() throws IOException {
        File file = new File(accountsStorageFile);
        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedReader fileReader = new BufferedReader(new FileReader(accountsStorageFile));
        Set<User> loadedUsers = new HashSet<>();

        String line;
        while((line = fileReader.readLine()) != null) {
            if (line.contains(":") && line.contains(";")) {
                line = line.replace(";", "");
                String[] parts = line.split(":");
                User user = new User(parts[0], parts[1]);
                loadedUsers.add(user);
            }
        }
        fileReader.close();
        return loadedUsers;
    }

}
