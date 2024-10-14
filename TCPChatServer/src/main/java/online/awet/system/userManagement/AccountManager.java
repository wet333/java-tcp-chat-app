package online.awet.system.userManagement;

/**
 * The {@code AccountManager} interface defines the contract for the most basic account management operations.
 * Implementations can provide different storage mechanisms (e.g., file-based, SQL database, etc.).
 */
public interface AccountManager {

    /**
     * Adds a new account with the specified username and password.
     *
     * @param username the username of the new account.
     * @param password the password of the new account.
     * @throws AccountManagerException if an account with the given username already exists or any other account creation error occurs.
     */
    void addAccount(String username, String password) throws AccountManagerException;

    /**
     * Deletes an account matching the given username and password.
     *
     * @param username the username of the account to delete.
     * @param password the password of the account to delete.
     * @throws AccountManagerException if the account does not exist or any other deletion error occurs.
     */
    void deleteAccount(String username, String password) throws AccountManagerException;

    /**
     * Retrieves an account by username and password.
     *
     * @param username the username of the account to retrieve.
     * @param password the password of the account to retrieve.
     * @return the {@code User} object if found.
     * @throws AccountManagerException if no matching user is found.
     */
    User getAccount(String username, String password) throws AccountManagerException;
}
