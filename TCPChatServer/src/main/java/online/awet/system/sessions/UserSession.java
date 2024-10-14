package online.awet.system.sessions;

import online.awet.system.sessions.data.BasicUserInfo;
import online.awet.system.sessions.data.CredentialsHolder;

import java.util.UUID;

public class UserSession implements Session {

    private final String sessionId;
    private BasicUserInfo basicUserInfo;
    private CredentialsHolder credentials;

    public UserSession() {
        sessionId = UUID.randomUUID().toString();
        basicUserInfo = new BasicUserInfo(null);
        credentials = new CredentialsHolder(null, null);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    // Get and Set methods for alias in BasicUserInfo
    public String getAlias() {
        if (basicUserInfo == null) {
            throw new IllegalStateException("Basic user info is not initialized.");
        }
        return basicUserInfo.alias();
    }

    public void setAlias(String alias) {
        if (basicUserInfo == null) {
            basicUserInfo = new BasicUserInfo(alias);
        } else {
            basicUserInfo = new BasicUserInfo(alias);
        }
    }

    // Get and Set methods for username in CredentialsHolder
    public String getUsername() {
        if (credentials == null) {
            throw new IllegalStateException("Credentials are not initialized.");
        }
        return credentials.username();
    }

    public void setUsername(String username) {
        if (credentials == null) {
            credentials = new CredentialsHolder(username, null);
        } else {
            credentials = new CredentialsHolder(username, credentials.password());
        }
    }

    // Get and Set methods for password in CredentialsHolder
    public String getPassword() {
        if (credentials == null) {
            throw new IllegalStateException("Credentials are not initialized.");
        }
        return credentials.password();
    }

    public void setPassword(String password) {
        if (credentials == null) {
            credentials = new CredentialsHolder(null, password);
        } else {
            credentials = new CredentialsHolder(credentials.username(), password);
        }
    }
}
