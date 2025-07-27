package online.awet.system.sessions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class AuthenticatedSession implements Session {

    private final String sessionId;
    private final Map<String, String> userInfo;

    public static final String USERNAME = "username";
    public static final String ALIAS = "alias";
    public static final String EMAIL = "email";

    public AuthenticatedSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.userInfo = new HashMap<>();
    }

    public AuthenticatedSession(String sessionId) {
        this.sessionId = sessionId;
        this.userInfo = new HashMap<>();
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getDisplayName() {
        String alias = hasUserInfo(ALIAS) ? getUserInfo(ALIAS) : null;
        if (alias != null && !alias.trim().isEmpty()) {
            return alias;
        }
        
        String username = hasUserInfo(USERNAME) ? getUserInfo(USERNAME) : null;
        if (username != null && !username.trim().isEmpty()) {
            return username;
        }
        
        return sessionId.substring(0, Math.min(8, sessionId.length()));
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    public String getUserInfo(String key) {
        return userInfo.get(key);
    }

    public Map<String, String> getAllUserInfo() {
        return new HashMap<>(userInfo);
    }

    public void setUserInfo(String key, String value) {
        userInfo.put(key, value);
    }

    public boolean hasUserInfo(String key) {
        return userInfo.containsKey(key);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AuthenticatedSession that = (AuthenticatedSession) obj;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return "AuthenticatedSession{" +
                "sessionId='" + sessionId.substring(0, Math.min(8, sessionId.length())) + "..." + '\'' +
                ", userInfo='" + getAllUserInfo() + '\'' +
                '}';
    }
}