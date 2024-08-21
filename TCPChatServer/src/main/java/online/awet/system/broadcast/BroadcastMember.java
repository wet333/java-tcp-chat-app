package online.awet.system.broadcast;

import java.io.BufferedWriter;
import java.util.Objects;

public class BroadcastMember {

    private final String sessionId;
    private final BufferedWriter writer;

    public BroadcastMember(String sessionId, BufferedWriter writer) {
        this.sessionId = sessionId;
        this.writer = writer;
    }

    public String getSessionId() {
        return sessionId;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    // Same sessionId same BroadcastMember
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BroadcastMember that = (BroadcastMember) o;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId);
    }
}
