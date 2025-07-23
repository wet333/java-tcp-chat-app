package online.awet.system.userManagement;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code AccountsManager} class is a singleton responsible for managing and persisting user accounts.
 * It implements the {@link AccountManager} interface using a file-based storage mechanism.
 *
 * <p>This implementation stores user accounts in a text file named {@code accounts.txt} and maintains
 * an in-memory {@link Set} of {@link User} objects for efficient access. The class provides methods
 * to add, delete, and retrieve user accounts.
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * AccountManager manager = AccountsManager.getInstance();
 * try {
 *     manager.addAccount("username123", "password123");
 *     User user = manager.getAccount("username123", "password123");
 *     manager.deleteAccount("username123", "password123");
 * } catch (AccountManagerException e) {
 *     // Handle account exception
 * }
 * }</pre>
 *
 * <p><strong>Note:</strong> This class uses the singleton pattern to ensure a single instance manages all accounts.
 * Future implementations can replace this with an SQL-based storage by creating another class
 * that implements the {@link AccountManager} interface.
 *
 * @see AccountManager
 * @see AccountManagerException
 */
public class FileStorageAccountManagerImpl implements AccountManager {

    /**
     * The file where user accounts are stored.
     */
    private static final String accountsStorageFile = "accounts.txt";

    /**
     * In-memory set of {@link User} objects.
     */
    private Set<User> users;

    /**
     * Singleton instance of {@code AccountsManager}.
     */
    private static FileStorageAccountManagerImpl instance;

    /**
     * Private constructor to load users into memory from the storage file and enforce singleton pattern.
     */
    private FileStorageAccountManagerImpl() {
        try {
            this.users = this.loadAccountList();
        } catch (IOException e) {
            System.out.println("I/O exception on File: " + accountsStorageFile);
            this.users = new HashSet<>();
        }
    }

    /**
     * Returns the singleton instance of the {@code AccountsManager}.
     *
     * @return the {@code AccountsManager} instance.
     */
    public static FileStorageAccountManagerImpl getInstance() {
        if (instance == null) {
            instance = new FileStorageAccountManagerImpl();
        }
        return instance;
    }

    /**
     * Adds a new account with the specified username and password.
     * If an account with the given username already exists, an {@link AccountManagerException} is thrown.
     *
     * @param username the username of the new account.
     * @param password the password of the new account.
     * @throws AccountManagerException if an account with the given username already exists.
     *
     * <p><strong>Example:</strong>
     * <pre>{@code
     * manager.addAccount("john_doe", "securePassword");
     * }</pre>
     */
    @Override
    public void addAccount(String username, String password) throws AccountManagerException {
        // Prevent duplicates of username
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                throw new AccountManagerException("Username already exists.");
            }
        }
        users.add(new User(username, password));
        try {
            saveAccountList();
        } catch (IOException e) {
            System.out.println("Error saving the user: " + username + ", couldn't open file: " + accountsStorageFile);
            // Optionally rethrow the exception or handle it appropriately
        }
    }

    /**
     * Deletes an account matching the given username and password.
     * If the account does not exist or the password is incorrect, an {@link AccountManagerException} is thrown.
     *
     * @param username the username of the account to delete.
     * @param password the password of the account to delete.
     * @throws AccountManagerException if the account does not exist or the password is incorrect.
     *
     * <p><strong>Example:</strong>
     * <pre>{@code
     * manager.deleteAccount("john_doe", "securePassword");
     * }</pre>
     */
    @Override
    public void deleteAccount(String username, String password) throws AccountManagerException {
        boolean removed = users.removeIf(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
        if (!removed) {
            throw new AccountManagerException("Account not found or incorrect password.");
        }
        try {
            saveAccountList();
        } catch (IOException e) {
            System.out.println("Error saving accounts after deletion.");
            // Optionally rethrow the exception or handle it appropriately
        }
    }

    /**
     * Retrieves an account by username and password.
     * If no matching user is found, an {@link AccountManagerException} is thrown.
     *
     * @param username the username of the account to retrieve.
     * @param password the password of the account to retrieve.
     * @return the {@link User} object if found.
     * @throws AccountManagerException if no matching user is found.
     *
     * <p><strong>Example:</strong>
     * <pre>{@code
     * User user = manager.getAccount("john_doe", "securePassword");
     * }</pre>
     */
    @Override
    public User getAccount(String username, String password) throws AccountManagerException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new AccountManagerException("Account not found or incorrect password.");
    }

    /**
     * Saves the in-memory user list to the account's storage file.
     *
     * <p>Each user is stored in the file in the format: {@code username:password;}
     *
     * @throws IOException if an I/O error occurs when writing to the file.
     */
    private void saveAccountList() throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(accountsStorageFile))) {
            for (User user : this.users) {
                fileWriter.write(user.getUsername() + ":" + user.getPassword() + ";" + System.lineSeparator());
            }
            fileWriter.flush();
        } catch (FileNotFoundException e) {
            System.out.println("Error saving users, File not found: " + accountsStorageFile);
            throw e;
        }
    }

    /**
     * Loads the user account list from the storage file into memory.
     *
     * <p>Each user is expected to be stored as {@code username:password;} in the file.
     *
     * @return a {@link Set} of {@link User} objects.
     * @throws IOException if an I/O error occurs when reading from the file.
     */
    private Set<User> loadAccountList() throws IOException {
        File file = new File(accountsStorageFile);
        if (!file.exists()) {
            file.createNewFile();
        }

        Set<User> loadedUsers = new HashSet<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(accountsStorageFile))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.contains(":") && line.contains(";")) {
                    line = line.replace(";", "");
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        User user = new User(parts[0], parts[1]);
                        loadedUsers.add(user);
                    }
                }
            }
        }
        return loadedUsers;
    }
}
