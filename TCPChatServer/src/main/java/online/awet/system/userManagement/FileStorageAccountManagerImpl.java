package online.awet.system.userManagement;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FileStorageAccountManagerImpl implements AccountManager {

    private static final String accountsStorageFile = "accounts.txt";

    private Set<User> users;

    private static FileStorageAccountManagerImpl instance;

    private FileStorageAccountManagerImpl() {
        try {
            this.users = this.loadAccountList();
        } catch (IOException e) {
            System.out.println("I/O exception on File: " + accountsStorageFile);
            this.users = new HashSet<>();
        }
    }

    public static FileStorageAccountManagerImpl getInstance() {
        if (instance == null) {
            instance = new FileStorageAccountManagerImpl();
        }
        return instance;
    }

    @Override
    public void addAccount(String username, String password) throws AccountManagerException {
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
        }
    }

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
        }
    }

    @Override
    public User getAccount(String username, String password) throws AccountManagerException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new AccountManagerException("Account not found or incorrect password.");
    }

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
