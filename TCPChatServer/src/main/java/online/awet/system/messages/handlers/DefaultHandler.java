package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.BaseMessageHandler;
import online.awet.system.messages.RegisterMessageHandler;
import online.awet.system.sessions.Session;

@RegisterMessageHandler
public class DefaultHandler extends BaseMessageHandler {

    @Override
    public boolean accepts(String message) {
        return true;
    }

    @Override
    public void handleMessage(Session session, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        String messageMod = session.getSessionId() + ": " + message;
        broadcastManager.broadcast(messageMod, session);
    }
}
