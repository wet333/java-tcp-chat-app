package online.awet.system.sessions;

import java.util.UUID;

public class GuestSession implements Session {

    private final String sessionId;

    public GuestSession() {
        sessionId = UUID.randomUUID().toString();
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
}
