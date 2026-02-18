package online.awet.system.sessions.holder;

import online.awet.system.sessions.AnonymousSession;
import online.awet.system.sessions.AuthenticatedSession;
import online.awet.system.sessions.Session;

import java.util.Map;

public class InMemorySessionHolder implements SessionHolder {

    private Session currentSession;

    public InMemorySessionHolder() {
        this.currentSession = new AnonymousSession();
    }

    @Override
    public Session getCurrentSession() {
        return currentSession;
    }

    @Override
    public void deleteSession() {
        this.currentSession = new AnonymousSession(this.currentSession.getSessionId());
    }

    @Override
    public void authenticate(Map<String, String> userInfo) {
        String currentSessionId = this.currentSession.getSessionId();
        AuthenticatedSession newSession = new AuthenticatedSession(currentSessionId);
        userInfo.forEach(newSession::setUserInfo);
        this.currentSession = newSession;
    }
}