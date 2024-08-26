package online.awet.system.messages;

import online.awet.system.sessions.Session;

public abstract class BaseMessageHandler implements MessageHandler {

    public abstract boolean accepts(String message);
    public abstract void handleMessage(Session session, String message);

    public void process(Session session, String message) {
        if (this.accepts(message)) {
            // TODO: use a logger
            System.out.println(this.getClass().getName() + " --will-handle--> " + message);
            this.handleMessage(session, message);
        }
    }
}
