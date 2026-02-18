package online.awet.system.userManagement;

public interface AccountManagerContract {

    void addAccount(String username, String password) throws AccountManagerException;

    void deleteAccount(String username, String password) throws AccountManagerException;

    User getAccount(String username, String password) throws AccountManagerException;

    void save(User user) throws AccountManagerException;

    void update(User user) throws AccountManagerException;

    User loadByUsername(String username) throws AccountManagerException;

    boolean existsByUsername(String username);
}
