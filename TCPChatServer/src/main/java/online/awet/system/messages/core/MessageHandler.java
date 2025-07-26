package online.awet.system.messages.core;

import online.awet.system.sessions.holder.SessionHolder;

public interface MessageHandler {

    void process(SessionHolder sessionHolder, String message);

    boolean accepts(String message);
}
