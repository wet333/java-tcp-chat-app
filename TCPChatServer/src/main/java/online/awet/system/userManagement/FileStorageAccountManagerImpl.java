package online.awet.system.userManagement;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileStorageAccountManagerImpl implements AccountManagerContract {

    private final Path storagePath;
    private final Set<User> users;

    public FileStorageAccountManagerImpl(Path storagePath) {
        this.storagePath = storagePath;
        Set<User> loaded;
        try {
            loaded = loadAccountList();
        } catch (IOException e) {
            System.out.println("I/O exception on File: " + storagePath);
            loaded = new HashSet<>();
        }
        this.users = loaded;
    }

    @Override
    public synchronized void addAccount(String username, String password) throws AccountManagerException {
        if (existsByUsername(username)) {
            throw new AccountManagerException("Username already exists.");
        }
        users.add(new User(username, password));
        try {
            saveAccountList();
        } catch (IOException e) {
            System.out.println("Error saving the user: " + username + ", couldn't open file: " + storagePath);
        }
    }

    @Override
    public synchronized void deleteAccount(String username, String password) throws AccountManagerException {
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
    public synchronized User getAccount(String username, String password) throws AccountManagerException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new AccountManagerException("Account not found or incorrect password.");
    }

    @Override
    public synchronized void save(User user) throws AccountManagerException {
        if (user == null) {
            throw new AccountManagerException("User cannot be null.");
        }
        users.removeIf(u -> u.getUsername().equals(user.getUsername()));
        User toAdd = new User(user.getUsername(), user.getPassword());
        if (user.getIp() != null) {
            toAdd.setIp(user.getIp());
        }
        users.add(toAdd);
        try {
            saveAccountList();
        } catch (IOException e) {
            throw new AccountManagerException("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public synchronized void update(User user) throws AccountManagerException {
        if (user == null) {
            throw new AccountManagerException("User cannot be null.");
        }
        User existing = loadByUsername(user.getUsername());
        existing.setPassword(user.getPassword());
        if (user.getIp() != null) {
            existing.setIp(user.getIp());
        }
        try {
            saveAccountList();
        } catch (IOException e) {
            throw new AccountManagerException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public synchronized User loadByUsername(String username) throws AccountManagerException {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        throw new AccountManagerException("Account not found: " + username);
    }

    @Override
    public synchronized boolean existsByUsername(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    private void saveAccountList() throws IOException {
        String path = storagePath.toString();
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path))) {
            for (User user : this.users) {
                fileWriter.write(user.getUsername() + ":" + user.getPassword() + ";" + System.lineSeparator());
            }
            fileWriter.flush();
        } catch (FileNotFoundException e) {
            System.out.println("Error saving users, File not found: " + path);
            throw e;
        }
    }

    private Set<User> loadAccountList() throws IOException {
        File file = storagePath.toFile();
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        Set<User> loadedUsers = new HashSet<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.contains(":") && line.contains(";")) {
                    line = line.replace(";", "");
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        User user = new User(parts[0], parts[1]);
                        loadedUsers.add(user);
                    }
                }
            }
        }
        return loadedUsers;
    }
}
