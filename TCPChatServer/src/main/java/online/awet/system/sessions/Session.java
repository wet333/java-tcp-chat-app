package online.awet.system.sessions;

public interface Session {

    String getSessionId();

    String getDisplayName();

    Boolean isAuthenticated();
}
