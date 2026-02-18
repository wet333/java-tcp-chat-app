package online.awet.system.sessions;

import java.util.Objects;
import java.util.UUID;

public final class AnonymousSession implements Session {

    private final String sessionId;

    public AnonymousSession() {
        this.sessionId = UUID.randomUUID().toString();
    }

    public AnonymousSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public String getDisplayName() {
        return sessionId.length() > 8 ? sessionId.substring(0, 8) : sessionId;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AnonymousSession that = (AnonymousSession) obj;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return "AnonymousSession{ sessionId='" + getDisplayName() + "'}";
    }
}