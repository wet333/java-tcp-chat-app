package online.awet.system.userManagement;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code AccountsManager} class is a singleton class responsible for managing and persisting
 * user accounts.
 * It handles account storage, retrieval, and modification. User data is saved to and loaded from
 * a text file, maintaining a set of {@code User} objects in memory.
 */
public class AccountsManager {

    /**
     * The file where user accounts are stored.
     */
    private static final String accountsStorageFile = "accounts.txt";

    /**
     * In-memory set of {@code User} objects.
     */
    private Set<User> users;

    /**
     * Singleton instance of {@code AccountsManager}.
     */
    private static AccountsManager instance;

    /**
     * Private constructor to load users into memory from the storage file and enforce singleton pattern.
     */
    private AccountsManager() {
        try {
            this.users = this.loadAccountList();
        } catch (IOException e) {
            System.out.println("I/O exception on File: " + accountsStorageFile);
        }
    }

    /**
     * Returns the singleton instance of the {@code AccountsManager}.
     *
     * @return the {@code AccountsManager} instance.
     */
    public static AccountsManager getInstance() {
        if (instance == null) {
            instance = new AccountsManager();
        }
        return instance;
    }

    /**
     * Adds a new account with the specified username and password, and saves the updated user list to the storage file.
     *
     * @param username the username of the new account.
     * @param password the password of the new account.
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * AccountsManager manager = AccountsManager.getInstance();
     * manager.addAccount("username123", "password123");
     * }
     * </pre>
     */
    public void addAccount(String username, String password) {
        // Prevent duplicates of username
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                throw new IllegalArgumentException("Username already exists.");
            }
        }
        users.add(new User(username, password));
        try {
            saveAccountList();
        } catch (IOException e) {
            System.out.println("There was an error saving the user: " + username + ", couldn't open file: " + accountsStorageFile);
        }
    }

    /**
     * Deletes an account matching the given username and password.
     *
     * @param username the username of the account to delete.
     * @param password the password of the account to delete.
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * AccountsManager manager = AccountsManager.getInstance();
     * manager.deleteAccount("username123", "password123");
     * }
     * </pre>
     */
    public void deleteAccount(String username, String password) {
        users.removeIf(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }

    /**
     * Searches for an account by username and password.
     *
     * @param username the username of the account to find.
     * @param password the password of the account to find.
     * @return the {@code User} object if a match is found, or {@code null} if no matching user is found.
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * AccountsManager manager = AccountsManager.getInstance();
     * User user = manager.findAccount("username123", "password123");
     * }
     * </pre>
     */
    public User getAccount(String username, String password) {
        User targetUser = null;
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                targetUser = user;
                break;
            }
        }
        return targetUser;
    }

    /**
     * Saves the in-memory user list to the account's storage file, with each user stored as "username:password;".
     *
     * @throws IOException if an I/O error occurs when writing to the file.
     */
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

    /**
     * Loads the user account list from the storage file into memory. Each user is expected to be stored
     * as "username:password;" in the file.
     *
     * @return a {@code Set} of {@code User} objects.
     * @throws IOException if an I/O error occurs when reading from the file.
     */
    public Set<User> loadAccountList() throws IOException {
        File file = new File(accountsStorageFile);
        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedReader fileReader = new BufferedReader(new FileReader(accountsStorageFile));
        Set<User> loadedUsers = new HashSet<>();

        String line;
        while ((line = fileReader.readLine()) != null) {
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
