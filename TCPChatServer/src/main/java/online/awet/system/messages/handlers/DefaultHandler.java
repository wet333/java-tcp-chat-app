package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.MessageHandlerFilterChain;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

@RegisterMessageHandler(priority = 0)
public class DefaultHandler extends BaseMessageHandler {

    @Override
    public boolean accepts(String message) {
        return true;
    }

    @Override
    public void handleMessage(SessionHolder sessionHolder, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session session = sessionHolder.getCurrentSession();

        if (ClientMessageParser.isACommand(message)) {
            message = "Invalid command: " + message + ". There are no handlers for this command.";
            broadcastManager.serverDirectMessage(message, session);
            return;
        }

        String messageMod = session.getDisplayName() + ": " + message;
        broadcastManager.broadcast(messageMod, session);
    }
}
