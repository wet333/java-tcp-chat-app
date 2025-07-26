package online.awet.system.messages.core;

import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

public abstract class BaseMessageHandler implements MessageHandler {

    public abstract boolean accepts(String message);

    public abstract void handleMessage(SessionHolder sessionHolder, String message) throws MessageHandlerException;

    public void process(SessionHolder sessionHolder, String message) {
        if (this.accepts(message)) {
            System.out.println(this.getClass().getName() + " --will-handle--> " + message);
            this.handleMessage(sessionHolder, message);
        }
    }
}
