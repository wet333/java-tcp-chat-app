package online.awet.system.messages;

import online.awet.system.sessions.Session;

public interface MessageHandler {
    public void process(Session session, String message);
}
