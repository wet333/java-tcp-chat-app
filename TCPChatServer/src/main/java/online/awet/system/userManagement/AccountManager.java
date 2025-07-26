package online.awet.system.userManagement;

public interface AccountManager {

    void addAccount(String username, String password) throws AccountManagerException;

    void deleteAccount(String username, String password) throws AccountManagerException;

    User getAccount(String username, String password) throws AccountManagerException;
}
