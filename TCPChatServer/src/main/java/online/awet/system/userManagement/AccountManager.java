package online.awet.system.userManagement;

public class AccountManager implements AccountManagerContract {

    private static final AccountManager instance = new AccountManager();

    private AccountManagerContract delegate;

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        return instance;
    }

    public static void setDelegate(AccountManagerContract implementation) {
        getInstance().delegate = implementation;
    }

    private AccountManagerContract getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("AccountManager delegate not set. Call AccountManager.setDelegate(implementation) at startup.");
        }
        return delegate;
    }

    @Override
    public void addAccount(String username, String password) throws AccountManagerException {
        getDelegate().addAccount(username, password);
    }

    @Override
    public void deleteAccount(String username, String password) throws AccountManagerException {
        getDelegate().deleteAccount(username, password);
    }

    @Override
    public User getAccount(String username, String password) throws AccountManagerException {
        return getDelegate().getAccount(username, password);
    }

    @Override
    public void save(User user) throws AccountManagerException {
        getDelegate().save(user);
    }

    @Override
    public void update(User user) throws AccountManagerException {
        getDelegate().update(user);
    }

    @Override
    public User loadByUsername(String username) throws AccountManagerException {
        return getDelegate().loadByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return getDelegate().existsByUsername(username);
    }
}
